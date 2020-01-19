package cluster.core.command.master;

import cluster.core.command.MasterCommand;

public final class CompleteTask implements MasterCommand {
	private final String sessionId;
	private final String message;

    public CompleteTask(String sessionId, String message) {
      this.sessionId = sessionId;
      this.message = message;
    }

	public String getSessionId() {
		return sessionId;
	}

	public String getMessage() {
		return message;
	}
  }