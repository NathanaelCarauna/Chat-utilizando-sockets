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
 * 
 * Sobre o Gerenciador de clientes:
 * 		- É gerado um novo gerenciador com uma nova thread para cada cliente;
 * 		- Gerencia a comunicação entre os clientes e entre o cliente e o servidor;
 * 		- A comunicação entre cliente e servidor é feita por meio de constantes(Strings), que estão na classe Comandos.
 * 			- O gerenciador identifica a String e executa o comando correspondente.
 */
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

public class GerenciadorDeClientes extends Thread{

	private Socket cliente;  // Declaração global da varíavel que armazena a instancia da conexão do servidor com o cliente.
	private String nomeCliente;  // Declaração global do nome do cliente da instancia.
	private BufferedReader leitor;  //Declaração global, Lê bytes recebidos em pacotes pela conexão  e converte para strings.
	private PrintWriter escritor;  //Declaração global, escreve dados que serão convertidos em bytes e enviados para o servidor.
	private static final Map<String,GerenciadorDeClientes> clientes = new HashMap<String,GerenciadorDeClientes>();  //Armazena as instancias dos clientes, tendo seus nomes como chaves.

	public GerenciadorDeClientes(Socket cliente ) {

		this.cliente = cliente;
		start();	//Inicia a thread para gerenciar mais de um cliente ao mesmo tempo
	}


	/**
	 * Método que vai gerenciar a conversa entre os clientes
	 */
	public void run() {

		try {		
			leitor = new BufferedReader(new InputStreamReader(cliente.getInputStream()));  // Lê bytes recebidos pela conexão em pacotes e converte para strings
			escritor = new PrintWriter(cliente.getOutputStream(), true);  // Escreve mensagens que serão enviadas para o cliente
			String mensagem;
			Date instanteAtual;
			Date ultimaMensangeEnviada;
			boolean usuarioEnviouMensagem = false;
			
			efetuarLogin();  // Solicita que o usuário informe dados
			ultimaMensangeEnviada = new Date();
			

			while(true) {  // Verifica constantemente se o cliente enviou algum input
				mensagem = leitor.readLine();  // Armazena a mensagem recebida do cliente
				
				
				instanteAtual = new Date();  // Armazena e mantem atualizado o horário
				
				if(usuarioEnviouMensagem) {  // se o usuario mandou mensagem
					ultimaMensangeEnviada = new Date();  // atualiza o horário da ultima mensagem enviada.
					usuarioEnviouMensagem = false;
				}
				
				// se a diferença entre o instante atual e o instante da ultima mensagem enviada for maior que 1 minuto
				if(instanteAtual.getMinutes() - ultimaMensangeEnviada.getMinutes() >=1) {  
					escritor.println(instanteAtual);  // enviar hora para o cliente.
					System.out.println("Data e hora atuais enviadas para " + this.nomeCliente);
					ultimaMensangeEnviada = new Date();  // armazenar novo instante
				}

				if(mensagem.equalsIgnoreCase(Comandos.SAIR)) {
					clientes.remove(this.nomeCliente);  // remove o usuário da lista
					this.cliente.close();  // encerra a conexão do cliente com o servidor
					System.out.println(this.nomeCliente + " encerrou a conexão.");
					System.out.println("Lista de clientes online: " + clientes.keySet().toString());

				}else if(mensagem.startsWith(Comandos.MENSAGEM)) {					
					String nomeDestinatario = mensagem.substring(Comandos.MENSAGEM.length(), mensagem.length());
					GerenciadorDeClientes destinatario = clientes.get(nomeDestinatario);
					usuarioEnviouMensagem = true;
					System.out.println(this.nomeCliente + " enviou mensagem para " + nomeDestinatario);


					if(destinatario == null) {						
						escritor.println("O usuário informado não existe");
						System.out.println(this.nomeCliente + "tentou enviar uma mensagem para um usuário inexistente.");

					}else {						
						destinatario.getEscritor().println(this.nomeCliente + " disse: " + leitor.readLine());
					}

					//Lista o nome de todos os usuarios logados
				}else if(mensagem.equals(Comandos.LISTAR_CONTATOS)) {
					System.out.println("Lista de usuários enviada para " + this.nomeCliente);
					atualizarListaUsuarios(this);
				}				
			}

		} catch (IOException e) {
			System.err.println(this.nomeCliente + " fechou a conexão");
			clientes.remove(this.nomeCliente);
			e.printStackTrace();
		}
	}

	private void efetuarLogin() throws IOException {
		while(true) {
			escritor.println(Comandos.LOGIN);  //Solicita ao cliente que digite um nome para login.		
			System.out.println("Servidor solicitou nome de usuário");
			this.nomeCliente = leitor.readLine().replaceAll(",", "");  // Remove virgulas do nome.

			if(this.nomeCliente.equalsIgnoreCase("null")) {   
				System.out.println("Usuário cancelou o login");
				escritor.println(Comandos.LOGIN_NULL);

			}else if(this.nomeCliente.isEmpty()) {
				System.out.println("Usuário não digitou nome");
				escritor.println(Comandos.LOGIN_EMPTY);

			}else if(clientes.containsKey(this.nomeCliente)) {
				System.out.println("Usuário inseriu um nome que já está em uso");
				escritor.println(Comandos.LOGIN_EM_USO);

			}
			else if(this.nomeCliente.equals(Comandos.SAIR)) {
				continue;
			}
			else {
				escritor.println(Comandos.LOGIN_ACEITO);
				escritor.println("Olá " + this.nomeCliente + ", "
						+ "\nSelecione um contato em sua lista para iniciar uma"
						+ "\nconversa.");
				clientes.put(this.nomeCliente, this);
				System.out.println("Login aceito, o novo usuário é " + this.nomeCliente);
				for(String cliente: clientes.keySet()) {
					atualizarListaUsuarios(clientes.get(cliente));
				}
				System.out.println(this.nomeCliente + " foi adicionado à lista de clientes online");
				System.out.println("Lista de clientes online: " + clientes.keySet().toString());
				break;
			}

		}

	}


	private void atualizarListaUsuarios(GerenciadorDeClientes cliente) {
		StringBuffer str = new StringBuffer();
		for(String c: clientes.keySet()){
			if(cliente.getNomeCliente().equals(c))
				continue;

			str.append(c);
			str.append(",");
		}
		if(str.length() > 0)
			str.delete(str.length()-1, str.length());
		cliente.getEscritor().println(Comandos.LISTAR_CONTATOS);
		cliente.getEscritor().println(str.toString());
	}



	public PrintWriter getEscritor() {
		return escritor;
	}

	public String getNomeCliente() {
		return nomeCliente;
	}
}
