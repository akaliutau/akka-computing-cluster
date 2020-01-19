package cluster.core;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import cluster.core.command.DataCommand;
import cluster.core.command.MasterCommand;
import cluster.core.command.WorkerCommand;
import cluster.core.command.data.CompleteProcess;
import cluster.core.command.worker.UnitExecCommand;
import cluster.core.command.worker.UnitResult;
import cluster.core.engine.Aggregator;
import cluster.core.model.AsyncData;
import static cluster.core.model.DPParams.UNIT_ID;

/**
 * The main class to seed the execution, distribute tasks among nodes and
 * collect the results The life cycle is ending After report to master
 * 
 * @author akaliutau
 *
 */
public final class ResultProcessor extends AbstractBehavior<DataCommand> {

	private final ActorRef<DataCommand> dataNode;
	private final Map<String, AsyncData> tasks;
	private final Aggregator aggregator;
	private final ActorRef<MasterCommand> masterNode;

	private ResultProcessor(ActorContext<DataCommand> context, List<AsyncData> splitWork, Aggregator aggregator,
			ActorRef<WorkerCommand> workers, ActorRef<DataCommand> dataNode, ActorRef<MasterCommand> masterNode) {
		super(context);
		this.dataNode = dataNode;
		this.masterNode = masterNode;
		this.tasks = new HashMap<>();
		for (AsyncData data : splitWork) {
			tasks.put(data.get(UNIT_ID), data);
		}
		this.aggregator = aggregator;

		ActorRef<UnitResult> responseAdapter = getContext().messageAdapter(UnitResult.class,
				processed -> new CompleteProcess(processed.getAsyncData(), masterNode));

		splitWork.stream().forEach(work -> workers.tell(new UnitExecCommand(work, responseAdapter)));

	}

	@Override
	public Receive<DataCommand> createReceive() {
		return newReceiveBuilder().onMessage(CompleteProcess.class, this::processResult).build();
	}

	private Behavior<DataCommand> processResult(CompleteProcess command) {// on each execution of unit
		getContext().getLog().info("complete unit " + command.getAsyncData().get(UNIT_ID));
		aggregator.accept(command.getAsyncData());

		if (aggregator.allDone()) {
			dataNode.tell(new CompleteProcess(aggregator.result(), masterNode));
			return Behaviors.stopped();
		} else {
			return this;
		}
	}

	/**
	 * Static utility methods
	 * 
	 * @param dataNode
	 * @param actorRef
	 * @return
	 */
	public static Behavior<DataCommand> create(List<AsyncData> splitWork, Aggregator aggregator,
			ActorRef<WorkerCommand> workers, ActorRef<DataCommand> dataNode, ActorRef<MasterCommand> masterNode) {
		return Behaviors.setup(context -> new ResultProcessor(context, splitWork, aggregator, workers, dataNode, masterNode));
	}

}
