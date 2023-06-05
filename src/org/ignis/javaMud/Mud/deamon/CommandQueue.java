package org.ignis.javaMud.Mud.deamon;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.ignis.javaMud.Mud.Engine;
import org.ignis.javaMud.Mud.Core.Player;
import org.ignis.javaMud.Mud.handlers.Handler;

public class CommandQueue implements Runnable, Handler{
	public static final String  REG_NAME = "commandQueue";
	private class Command{
		private String command;
		private Player player;
		private Command(String command, Player player) {
			super();
			this.command = command;
			this.player = player;
		}
		public String getCommand() {
			return command;
		}
		public Player getPlayer() {
			return player;
		}
	}
	private LinkedBlockingQueue<Command> queue;
	private Thread thread;
	
    public void run() {
        try {
        	while(!(Thread.currentThread().isInterrupted())) {
                Command command = queue.take();
                Player p = command.getPlayer();
                if(p!=null) {
                	p.processText(command.getCommand());
                }
            }
            Thread.currentThread().interrupt();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    public void addCommand(Player player, String command) {
    	try {
        	Command c = new Command(command, player);
			queue.put(c);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    }
	public CommandQueue() {
		queue = new LinkedBlockingQueue<>();
	}
	@Override
	public void init(Engine e) {
		thread = new Thread(this);
		thread.start();
	}
	@Override
	public void dest() {
		thread.interrupt();
	}
}
