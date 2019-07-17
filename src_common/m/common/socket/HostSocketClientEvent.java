package m.common.socket;

import m.system.socket.SocketClientEvent;

public class HostSocketClientEvent implements SocketClientEvent {

	public void closeCallback(String ip, int port) {
		//System.out.println("-----client_close");
	}

	public void openCallback(String ip, int port) {
		//System.out.println("-----client_open");
	}

	public void sendCallback(String ip, int port, byte[] result) {
		//System.out.println("-----client_send");
	}

}
