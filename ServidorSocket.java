/**
 * @author Nathanael Caraúna
 * 
 * Instruções para rodar o chat:
 * 	1. Rodar primeiro o ServidorSocket
 *		- Caso a porta padrão esteja em uso, alterar o valor na variável "porta".
 *
 * 	2. Rodar o Cliente
 * 		- Digitar o ip e a porta correspondente nas janelas que surgirão.
 * 			- Caso o servidor e o cliente estejam na mesma máquina (localhost) -> ip = 127.0.0.1
 * 			- Porta padrão do servidor -> 9997 (caso tenha sido alterada manualmente, usar a porta correspondente)
 * 		- Digitar um nome.
 * 
 * 	3. Rodar outro cliente.
 * 		- Por padrão, um cliente só conseguirá enviar uma mensagem para outro cliente.
 * 
 * Sobre o servidor:
 * 		- Escuta uma porta estabelecida e para cada nova conexão com o cliente,
 * 		chama um novo GerenciadorDeClientes, abrindo uma nova thread.
 */

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServidorSocket {
	public static void main(String[] args) {
		
		ServerSocket servidor = null;
		int porta = 9997;  // Porta que será escutada.
		
		try {
			System.out.println("Inicializando o servidor");
			servidor = new ServerSocket(porta);  // Escuta a porta inicializada acima.
			System.out.println("Servidor inicializado");
			System.out.println("Ouvindo a porta: " + porta);

			while(true) {			
				System.out.println("Esperando conexão de clientes");
				Socket cliente = servidor.accept();  // Fica aguardando um cliente se conectar, armazena o cliente conectado
				System.out.println("Novo cliente se conectou ao servidor.");
				new GerenciadorDeClientes(cliente);  // Cliente é passado para o gerenciador continuar a conexão
			}

		}catch(IOException e) {

			try {
				if(servidor != null) {
					servidor.close();
				}
				
			}catch(IOException e1) {}
			
			System.err.println("A porta " + porta + " está ocupada");
			e.printStackTrace();
		}
	}
}
