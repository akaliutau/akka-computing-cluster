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
				.onAnyMessage(this::onAny)
				.build();
	}

	private Behavior<WorkerCommand> onTimeout() {// invoked on clean up message
		cache.clear();
		return this;
	}
	
	private Behavior<WorkerCommand> onAny(Object msg) {// invoked on clean up message TODO: delete this
		System.out.println("Worker got a " + msg.toString());
		return this;
	}


	private Behavior<WorkerCommand> process(UnitExecCommand command) {
		getContext().getLog().info("Worker processing request [{}]", command.getWork());
		// put here execution functionality
		// this is going to be a blocking code of course
		getContext().getLog().info("Worker finished his work");
		
		command.getCallback().tell(new UnitResult(command.getWork()));
		return this;
	}
}
