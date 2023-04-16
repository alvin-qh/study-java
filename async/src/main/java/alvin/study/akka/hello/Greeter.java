package alvin.study.akka.hello;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;

public class Greeter extends AbstractBehavior<Greeter.Greet> {
    public static final class Greet {
        private final String whom;
        private final ActorRef<Greeted> replyTo;

        public Greet(String whom, ActorRef<Greeted> replyTo) {
            this.whom = whom;
            this.replyTo = replyTo;
        }

        public String getWhom() { return whom; }

        public ActorRef<Greeted> getReplyTo() { return replyTo; }
    }

    public static final class Greeted {
        private final String whom;
        private final ActorRef<Greet> from;

        public Greeted(String whom, ActorRef<Greet> from) {
            this.whom = whom;
            this.from = from;
        }

        public String getWhom() { return whom; }

        public ActorRef<Greet> getFrom() { return from; }
    }

    private Greeter(ActorContext<Greet> context) {
        super(context);
    }

    @Override
    public Receive<Greet> createReceive() {
        return newReceiveBuilder().onMessage(Greet.class, cmd -> {
            return this;
        }).build();
    }

    public static Behavior<Greet> create() {
        return Behaviors.setup(Greeter::new);
    }
}
