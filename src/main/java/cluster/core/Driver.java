package cluster.core;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import akka.actor.typed.ActorRef;
import akka.actor.typed.ActorSystem;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Routers;
import akka.actor.typed.receptionist.Receptionist;
import akka.actor.typed.receptionist.ServiceKey;
import akka.cluster.typed.Cluster;
import cluster.core.command.DataCommand;
import cluster.core.command.MasterCommand;
import cluster.core.command.WorkerCommand;
import cluster.core.command.master.InitExecutionSession;
import cluster.core.event.SessionEvent;
import cluster.core.model.AsyncData;
import cluster.core.model.DistributedProcess;

import static cluster.core.model.DPParams.*;

public class Driver {

	// https://doc.akka.io/docs/akka/current/typed/routers.html
	static final ServiceKey<DataCommand> STATS_SERVICE_KEY = ServiceKey.create(DataCommand.class,
			"ComputeService");

	public static Behavior<Void> create() {
		return Behaviors.setup(context -> {
			Cluster cluster = Cluster.get(context.getSystem());
			System.out.println("available roles:" + cluster.selfMember().getRoles());
			if (cluster.selfMember().hasRole("compute")) {

				// on every compute node there is one service instance that delegates to N local
				// workers
				final int numberOfWorkers = context.getSystem().settings().config()
						.getInt("stats-service.workers-per-node");
				Behavior<WorkerCommand> workerPoolBehavior = Routers
						.pool(numberOfWorkers, Worker.create().<WorkerCommand>narrow())
						.withRoundRobinRouting();
				ActorRef<WorkerCommand> workers = context.spawn(workerPoolBehavior, "ComputeServiceRouter");
				ActorRef<DataCommand> service = context.spawn(DataNode.create(workers.narrow()),
						"ComputeService");

				// published through the receptionist to the other nodes in the cluster
				context.getSystem().receptionist().tell(Receptionist.register(STATS_SERVICE_KEY, service.narrow()));
				

			}
			if (cluster.selfMember().hasRole("master")) {
				System.out.println(Routers.group(STATS_SERVICE_KEY).narrow().toString());
				ActorRef<DataCommand> serviceRouter = context.spawn(Routers.group(STATS_SERVICE_KEY),
						"ComputeServiceRouter"); // NOTE: here different key is used!
				ActorRef<MasterCommand> masterNode = context.spawn(MasterNode.create(serviceRouter), "masterNode");
				ActorRef<SessionEvent> event = context.spawn(SessionEventProcessor.create(), "emitter");
				context.watch(event);
				
				Thread.sleep(5000);// wait for 5 s to routers spin up
				
				AsyncData data = new AsyncData().addEntry(PROCESS_NAME, "demo process");
				DistributedProcess process = new DistributedProcess(data);
				
				masterNode.tell(new InitExecutionSession(process, event));


			}
//			return Behaviors.receive(Void.class).onSignal(Terminated.class, sig -> Behaviors.stopped()).build();
			return Behaviors.empty();
		});
	}

	public static void main(String[] args) throws InterruptedException {
		if (args.length == 0) {
			startup("compute", 25251);
			startup("compute", 25252);
			startup("master", 0);
		} else {
			if (args.length != 2)
				throw new IllegalArgumentException("Usage: role port");
			startup(args[0], Integer.parseInt(args[1]));
		}
	}

	private static void startup(String role, int port) {

		// Override the configuration of the port
		Map<String, Object> overrides = new HashMap<>();
		overrides.put("akka.remote.artery.canonical.port", port);
		overrides.put("akka.cluster.roles", Collections.singletonList(role));

		Config config = ConfigFactory.parseMap(overrides).withFallback(ConfigFactory.load("stats"));

		ActorSystem<Void> system = ActorSystem.create(create(), "ClusterSystem", config);
	}

}
