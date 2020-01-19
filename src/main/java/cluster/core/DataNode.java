package cluster.core;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import cluster.core.command.DataCommand;
import cluster.core.command.WorkerCommand;
import cluster.core.command.data.CompleteProcess;
import cluster.core.command.data.ComputeProcess;
import cluster.core.command.master.CompleteTask;
import cluster.core.engine.Aggregator;
import cluster.core.engine.ObjectCreator;
import cluster.core.engine.Splitter;
import cluster.core.model.AsyncData;
import static cluster.core.model.DPParams.SPLITTER;
import static cluster.core.model.DPParams.AGGREGATOR;
import static cluster.core.model.DPParams.SESSION_ID;


/**
 * The main class to seed the execution, distribute tasks among nodes and collect the results
 * The life cycle is ending After report to master
 * 
 * @author akaliutau
 *
 */
public final class DataNode extends AbstractBehavior<DataCommand> {

	private final ActorRef<WorkerCommand> workers;

	private DataNode(ActorContext<DataCommand> context, ActorRef<WorkerCommand> workers) {
		super(context);
		this.workers = workers;
	}

	@Override
	public Receive<DataCommand> createReceive() {
		return newReceiveBuilder()
				.onMessage(ComputeProcess.class, this::process)
				.onMessage(CompleteProcess.class, this::finish)
				.build();
	}

	private Behavior<DataCommand> process(ComputeProcess command) {// 
		getContext().getLog().info("Delegating request, splitting job into smaller subtasks");
		AsyncData data = command.getAsyncData();
		Splitter splitter = ObjectCreator.create(data.getOrDefault(SPLITTER, "cluster.core.engine.IdentitySplitter"));
		Aggregator aggregator = ObjectCreator.create(data.getOrDefault(AGGREGATOR, "cluster.core.engine.SimpleAggregator"));
		
		getContext().spawnAnonymous(ResultProcessor.create(splitter.split(data), aggregator, workers, command.getDataNodeRef(), command.getMasterNodeRef()));
		return this;
	}
	
	private Behavior<DataCommand> finish(CompleteProcess command) {// executed when all have been done
		getContext().getLog().info("all units completed their work, sending CompleteTask message to master node");
		command.getMasterRef().tell(new CompleteTask(command.getAsyncData().get(SESSION_ID), "process complete"));
		return this;
	}
	
	/**
	 * Static utility methods
	 * @param actorRef
	 * @return
	 */
	public static Behavior<DataCommand> create(ActorRef<WorkerCommand> actorRef) {
		return Behaviors.setup(context -> new DataNode(context, actorRef));
	}


}

