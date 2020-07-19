import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class ClienteSocket extends JFrame{
public static void main(String[] args) {
		
		try {
//			Definindo endereço e porta para conexão
			Socket cliente = new Socket("127.0.0.1", 9997);
			
//			Lendo mensagens do servidor
			new Thread() {
				public void run() {
					try {
						BufferedReader leitor = new BufferedReader(new InputStreamReader(cliente.getInputStream()));
			
						
						while(true) {
							String mensagem = leitor.readLine();
							if(mensagem == null || mensagem.isEmpty()) {
								continue;
							}
							System.out.println("O servidor disse -> " + mensagem);
						}
						
					} catch (IOException e) {
						System.out.println("Não foi possível ler a mensagem do servidor");
						e.printStackTrace();
					}
				};
				
			}.start();
			
//			Escrevendo para o servidor
			PrintWriter escritor = new PrintWriter(cliente.getOutputStream(), true);
//			Leitor do terminal
			BufferedReader leitorTerminal = new BufferedReader(new InputStreamReader(System.in));
			String mensagemTerminal = "";
			while(true) {
				mensagemTerminal = leitorTerminal.readLine();
				escritor.println(mensagemTerminal);
				if(mensagemTerminal.equalsIgnoreCase("//SAIR")) {
					System.exit(0);
				}
			}
			
		}catch(UnknownHostException e) {
			System.out.println("O endereço passado é inválido");
			e.printStackTrace();
		}
		catch(IOException e) {
			System.out.println("Não foi possivel realizar conexão, servidor pode estar fora do ar");
			e.printStackTrace();
		}
	}
}
