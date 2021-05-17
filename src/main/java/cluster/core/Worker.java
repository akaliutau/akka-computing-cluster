package cluster.core;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import cluster.core.command.WorkerCommand;
import cluster.core.command.worker.TimeOut;
import cluster.core.command.worker.UnitExecCommand;
import cluster.core.command.worker.UnitResult;
import cluster.core.engine.ObjectCreator;
import cluster.core.engine.api.Processor;
import cluster.core.model.AsyncData;

import static cluster.core.model.DPParams.PROCESSOR;

/**
 * Encapsulates logic related to execution of class on one computing node
 * 
 * @author akaliutau
 */
public final class Worker extends AbstractBehavior<WorkerCommand> {

	private final Map<String, Integer> cache = new HashMap<String, Integer>();

	private Worker(ActorContext<WorkerCommand> context) {
		super(context);
	}

	public static Behavior<WorkerCommand> create() {
		return Behaviors.setup(context -> Behaviors.withTimers(timers -> {
			context.getLog().info("Worker starting up");
			// setup limit for one job execution
			timers.startTimerWithFixedDelay(TimeOut.INSTANCE, TimeOut.INSTANCE, Duration.ofSeconds(30));// health check on worker
																												
			return new Worker(context);
		}));
	}

	@Override
	public Receive<WorkerCommand> createReceive() {
		return newReceiveBuilder()
				.onMessage(UnitExecCommand.class, this::process)
				.onMessageEquals(TimeOut.INSTANCE, this::onTimeout)
				.build();
	}

	private Behavior<WorkerCommand> onTimeout() {// invoked on clean up message
		cache.clear();
		return this;
	}
	
	private Behavior<WorkerCommand> process(UnitExecCommand command) {
		getContext().getLog().info("Worker processing request [{}]", command.getWork());
		AsyncData data = command.getWork();
		Processor proc = ObjectCreator.create(data.getOrDefault(PROCESSOR, "cluster.core.engine.NoneProcessor"));
		// NOTE: process method is a blocking code
		command.getCallback().tell(new UnitResult(proc.process(command.getWork())));
		getContext().getLog().info("Worker finished his work");
		return this;
	}
}
