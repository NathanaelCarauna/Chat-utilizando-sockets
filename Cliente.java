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
 * Sobre o Cliente:
 * 		- 
 */

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.Timer;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;

public class Cliente extends JFrame{

	/**
	 * Dados para estabelecer conexão
	 */
	static String ip = "127.0.0.1";
	static int porta = 9997;

	/**
	 * Declaração de variáveis que vão ser usadas globalmente.
	 */
	private static final long serialVersionUID = -7758098849417253993L;
	private PrintWriter escritor;  
	private BufferedReader leitor;  
	private JFrame frmMensageiro;  
	private JList listContato;  
	private JTextArea txtrEditor;  
	private JTextArea txtrVisor; 
	private JLabel lblChatBoasvindas; 
	private JButton btnEnviar;
	private JButton btnSair;

	public Cliente() {
		
		System.out.println("Pedindo usuário por IP do servidor");
		ip = JOptionPane.showInputDialog("Qual o endereço de IP do servidor? (\"127.0.0.1\")");
		if(ip == null) {
			System.out.println("O usuário fechou antes de digitar o IP");
			System.exit(0);
		}
		
		System.out.println("Pedindo usuário pela porta de conexão.");
		porta = Integer.parseInt(JOptionPane.showInputDialog("Qual a porta? (\"9997\")"));
		if(porta == 0) {
			System.out.println("O usuário fechou antes de digitar o endereço");
			System.exit(0);
		}
		
		System.out.println("Inicializando interface");
		initialize();  // Inicializa os componentes gráficos.
	}

	/**
	 * Método sem parâmetros, que define todos os componentes gráficos.
	 */
	private void initialize() {

		frmMensageiro = new JFrame();  // Tela principal.
		frmMensageiro.getContentPane().setBackground(new Color(230, 230, 250));  // Define a cor  de fundo da tela principal.
		frmMensageiro.setTitle("Mensageiro");  // Define o título da janela.
		frmMensageiro.setBounds(100, 100, 649, 481);  // Define o tamanho da janela.
		frmMensageiro.setResizable(false);  // Desabilita o redimensionamento da janela.
		frmMensageiro.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  // Define o comportamento ao fechar para encerrar o processo.
		frmMensageiro.getContentPane().setLayout(null);  // Define o layout da tela principal como absoluta.
		frmMensageiro.setLocationRelativeTo(null);  // Janela é aberta no centro.

		JScrollPane scrollList = new JScrollPane();  // Painel com scroll que armazena a lista de contatos.
		scrollList.setBounds(12, 41, 133, 380);  // Define a posição e o tamanho.
		frmMensageiro.getContentPane().add(scrollList);  // Adiciona o painel scroll da lista à jánela principal.

		JScrollPane scrollVisor = new JScrollPane();  // Painel com scroll que armazena o painel de conversa.
		scrollVisor.setBounds(156, 71, 463, 254);  // Define posição e tamanho.
		frmMensageiro.getContentPane().add(scrollVisor);  // Adiciona o painel scroll de conversa à janela principal.

		JPanel panelLblContato = new JPanel();  // Painel que armazena a label "Contatos".
		panelLblContato.setBorder(new LineBorder(Color.DARK_GRAY, 2, true));  // Ativa a borda, define a cor e a espessura.
		panelLblContato.setBackground(Color.BLACK);  // Define a cor de fundo do painel.  
		scrollList.setColumnHeaderView(panelLblContato);  // Adiciona o painel no painel scroll da lista.

		JPanel panelEditor = new JPanel();  // Painel que armazena a área de texto onde o usuário digita.
		panelEditor.setBorder(new LineBorder(new Color(0, 0, 0), 2));  //Define a borda.
		panelEditor.setBounds(157, 339, 360, 82);  // Define a posição e o tamanho.
		panelEditor.setLayout(new BorderLayout(0, 0));  // Define o layout do painel.
		frmMensageiro.getContentPane().add(panelEditor);  //Adiciona o painel à janela principal.

		JPanel panelLblChat = new JPanel();  // Painel que armazena a label de "Bem Vindo" do chat. 
		panelLblChat.setBackground(Color.BLACK);  // Define a cor de fundo.
		panelLblChat.setBorder(new LineBorder(new Color(0, 0, 0), 2));  // Define a borda.
		panelLblChat.setBounds(157, 41, 462, 31);  // Define a posição e o tamanho.
		frmMensageiro.getContentPane().add(panelLblChat);  // Adiciona o painel à tela principal.
		panelLblChat.setLayout(null);  // Define o layout como absoluto.

		JLabel lblContatos = new JLabel("Contatos");  // Label para identificar a lista de contatos visualmente.
		lblContatos.setForeground(Color.WHITE);  // Define a cor da fonte.
		lblContatos.setFont(new Font("Tahoma", Font.BOLD, 14));  // Define a fonte da label.
		panelLblContato.add(lblContatos);  // Adiciona a label ao painel armazenado no scroll.

		listContato = new JList<String>();  // Lista que vai receber os contatos do servidor.
		listContato.setBorder(new MatteBorder(1, 2, 2, 2, (Color) new Color(0, 0, 0)));  //Define a espessura da borda e sua cor.
		listContato.setBackground(Color.DARK_GRAY);  // Define a cor de fundo da lista.
		listContato.setForeground(Color.WHITE);  // Define a cor da fonte.
		listContato.setFont(new Font("Tahoma", Font.BOLD, 13));  //Define a fonte da lista.
		scrollList.setViewportView(listContato);  // Adiciona a lista ao painel scroll.

		txtrVisor = new JTextArea();  // Tela que exibe os textos da conversa.
		txtrVisor.setBounds(157, 74, 462, 252);  // Define a posição e o tamanho da tela de exibição.
		txtrVisor.setFont(new Font("Monospaced", Font.BOLD, 13));  // Define a fonte e seu estilo.
		txtrVisor.setEditable(false);  // Desabilita a edição de texto.
		txtrVisor.setLineWrap(true);  // Passa o cursor de inserção para a próxima linha quando é atingindo a largura do componente
		scrollVisor.setViewportView(txtrVisor);  // Adicionar a tela de exibição ao painel scroll.

		txtrEditor = new JTextArea();  // Área em que o usuário digita.
		txtrEditor.setFont(new Font("Monospaced", Font.BOLD, 13));  // Define a fonte.
		txtrEditor.setForeground(Color.BLACK);  // Define a cor da fonte.
		txtrEditor.setBackground(Color.LIGHT_GRAY);  // Define a cor de fundo.
		txtrEditor.setText("Digite sua mensagem aqui");  //Define o texto inicial.
		txtrEditor.setLineWrap(true);  // Passa o cursor de inserção para a próxima linha quando é atingindo a largura do componente
		panelEditor.add(txtrEditor);  //Adiciona a área de texto editável pelo usuário.

		btnEnviar = new JButton("Enviar");  // Botão com função de enviar a mensagem para o servidor.
		btnEnviar.setForeground(Color.WHITE);  // Define cor da fonte.
		btnEnviar.setBackground(Color.DARK_GRAY);  // Define cor de fundo do botão.
		btnEnviar.setBounds(529, 350, 90, 25);  // Define a posição e o tamanho.
		frmMensageiro.getContentPane().add(btnEnviar);  // Adiciona o botão à tela principal.

		JButton btnAnexo = new JButton("Anexo");  // Abre popup solicitando a imagem.
		btnAnexo.setForeground(Color.WHITE);  // Define a cor da fonte.
		btnAnexo.setBackground(Color.DARK_GRAY);  // Define a cor de fundo.
		btnAnexo.setBounds(529, 385, 90, 25);  // Define a posição e o tamanho.
		btnAnexo.setEnabled(false);  // Desabilita a função.
		frmMensageiro.getContentPane().add(btnAnexo);  //Adiciona o botão à tela princial

		btnSair = new JButton("Sair");  // Botão para fechar a janela apropriadamente.
		btnSair.setForeground(Color.WHITE);  // Define a cor da fonte
		btnSair.setBackground(Color.DARK_GRAY);  // Define a cor de fundo
		btnSair.setBounds(529, 13, 90, 24);  //Define a posição e o tamanho.
		frmMensageiro.getContentPane().add(btnSair);  // Adiciona à tela principal.

		lblChatBoasvindas = new JLabel("Bem Vindo!");  // Label de boas vindas.
		lblChatBoasvindas.setForeground(Color.WHITE);  // Define a cor da fonte.
		lblChatBoasvindas.setHorizontalAlignment(SwingConstants.CENTER);  // Define o alinhamento.
		lblChatBoasvindas.setFont(new Font("Tahoma", Font.BOLD, 14));  // Define a fonte.
		lblChatBoasvindas.setBounds(0, 0, 462, 29);  // Define a posição e o tamanho.
		panelLblChat.add(lblChatBoasvindas);  // Adiciona a label ao painel dela.

	}

	/**
	 * Método que recebe um vetor de strings contendo os nomes dos usuários online,
	 * e os adiciona ao componente de interface que os exibirá como uma lista.
	 * @param usuarios
	 */
	private void preencherListaUsuarios(String[] usuarios) {
		System.out.println("String de usuários online recebida do servidor");
		DefaultListModel<String> modelo = new DefaultListModel<String>();  //Cria um modelo de lista padrão do tipo string.
		listContato.setModel(modelo);  // Adiciona o modelo ao componente de interface.
		for(String usuario: usuarios) {  // Laço que percorre o vetor recebido como parâmentro.
			modelo.addElement(usuario);  // Adiciona cada elemento percorrido ao modelo que será exibido no componente gráfico "listContato".
		}
		System.out.println("Usuários onlines adicionados à lista da interface.");
		
//		for(int i = 0; i< modelo.getSize(); i++) {
//			for(int j = 0; j<usuarios.length; j++) {
//				if(modelo.getElementAt(i) == usuarios[j]) {
//					continue;
//				}else {
//					modelo.removeElementAt(i);
//				}
//			}
//		}
	}

	/**
	 * Método sem retorno e parâmetro, que gerencia inputs do usuário e envia para o servidor.
	 */
	private void gerenciadorDeInputs() {		
		btnSair.addActionListener(new java.awt.event.ActionListener() {  //Encerra a conexão com o servidor ao clicar no botão "sair".
			public void actionPerformed(java.awt.event.ActionEvent e) {
				System.out.println("Usuário acionou encerramento.");
				escritor.println(Comandos.SAIR);
				try {
					leitor.close();
					System.out.println("Leitor encerrado");
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				System.exit(0);
			}
		});
		btnSair.addMouseListener(new java.awt.event.MouseAdapter() {  // Muda a cor do botão sair, ao cursor estar sobre o botão.
			public void mouseEntered(java.awt.event.MouseEvent evt) {
				System.out.println("Mouse sobre botão sair");
				btnSair.setBackground(new Color (255,51,51));
			}
			public void mouseExited(java.awt.event.MouseEvent evt) {  // Retorna a cor do botão para o padrão, ao cursor sair de cima dele.
				System.out.println("Mouse não está mais sobre o botão sair");
				btnSair.setBackground(Color.DARK_GRAY);;
			}
		});

		btnEnviar.addActionListener(new java.awt.event.ActionListener() {	// Enviar mensagem clicando no botão enviar

			public void actionPerformed(java.awt.event.ActionEvent evt) {  
				if(!txtrVisor.getText().isEmpty()) {  // Só enviar mensagem se o editor não estiver vazio
					System.out.println("Botão enviar acionado.");
					gerenciadorMensagens(evt);
					txtrVisor.append("\n");
				}
			}
		});

		btnEnviar.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseEntered(java.awt.event.MouseEvent evt) {  // Muda a cor do botão "enviar" quando o cursor estiver sobre o botão.
				System.out.println("Mouse está sobre botão enviar");
				btnEnviar.setBackground(new Color (0,153,51));
			}
			public void mouseExited(java.awt.event.MouseEvent evt) {  // Retorna a cor do botão ao padrão, quando o cursor sair de cima dele.
				System.out.println("Mouse não está mais sobre o botão enviar");
				btnEnviar.setBackground(Color.DARK_GRAY);
			}
		});


		txtrEditor.addKeyListener(new KeyListener() {

			public void keyTyped(KeyEvent e) {}

			public void keyReleased(KeyEvent e) {
				if(e.getKeyCode() == KeyEvent.VK_ENTER) {  // Enviar mensagem quando a tecla "ENTER" for solta
					if(!txtrVisor.getText().isEmpty()) {
						btnEnviar.setBackground(Color.DARK_GRAY);
						gerenciadorMensagens(e);
					}
				}				
			}

			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode() == KeyEvent.VK_ENTER) {  // Mudar cor do botão enviar quando a tecla "ENTER" for pressionada
					System.out.println("Tecla ENTER pressionada");
					btnEnviar.setBackground(new Color (0,153,51));  				
				}
			}
		});

	}

	/**
	 * Envia mensagens para os servidor/outro cliente. Recebe como parâmetro um evento que funciona como gatilho para que a mensagem seja enviada
	 * @param e
	 */
	protected void gerenciadorMensagens(Object e) {

		if(txtrEditor.getText().isEmpty() || txtrEditor.getText().equals("\n")) {  // Verifica se o usuário escreveu algo
			System.out.println("usuário tentou enviar mensagem vazia");
			txtrEditor.setText("");
			return;
		}

		Object usuario = listContato.getSelectedValue();  // armazena o contato selecionado com o mouse em uma variável.
		if(usuario != null) {

			txtrVisor.append("Eu: ");
			txtrVisor.append(txtrEditor.getText());  //Joga a mensagem para ser exibida no visor.
			System.out.println("Mensagem digitada adicionada ao visor.");
			
			escritor.println(Comandos.MENSAGEM + usuario);  //Direciona a mensagem para o contato selecionado.
			escritor.println(txtrEditor.getText());	 // Envia o que está no editor para o servidor.
			System.out.println("Mensagem encaminhada para o servidor");
			txtrEditor.setText("");  // Limpa o editor depois de enviar uma mensagem.

		}else {					
			JOptionPane.showMessageDialog(Cliente.this, "Selecione um usuário");
			System.out.println("Usuário tentou enviar mensagem sem selecionar um usuário");
			return;
		}
	}

	
	/**
	 * Método que estabelece conexão com um servidor por meio de socket
	 */
	public void estabelecerConexao() {

		try {
			System.out.println("Tentando se conectar ao servidor");
			final Socket cliente = new Socket(ip, porta);  //Definindo endereço e porta para conexão
			escritor = new PrintWriter(cliente.getOutputStream(), true);  //Escreve dados que serão convertidos em bytes e enviados para o servidor.
			leitor = new BufferedReader(new InputStreamReader(cliente.getInputStream()));  // Lê bytes recebidos em pacotes pela conexão  e converte para strings.

		}catch(UnknownHostException e) {
			System.out.println("Endereço inválido");
			e.printStackTrace();
		}catch(IOException e) {
			System.out.println("Não foi possivel realizar conexão, servidor pode estar fora do ar");
			e.printStackTrace();
		}
	}

	/**
	 * Envia o comando para o servidor, para que ele envie o vetor de contatos online.
	 */
	private void pedirListaUsuarios() {
		escritor.println(Comandos.LISTAR_CONTATOS);
		System.out.println("Lista de contatos solicitada do servidor");
	}

	/**
	 * Gerencia as mensagens vindas do servidor.
	 */
	private void iniciarLeitor() {
		try {
			while(true) {

				String mensagem = leitor.readLine();  // Armazena a mensagem do servidor na variável mensagem.

				if(mensagem == null || mensagem.isEmpty()) {  // Verifica se a mensagem é nula ou está vazia.
					System.out.println("Usuário recebeu uma mensagem nula/vazia");
					continue;  // Ignora a mensagem.

				}else if(mensagem.equals(Comandos.LISTAR_CONTATOS)) {  // Verifica se a mensagem é a resposta de um comando com a lista de contatos. 
					String[] usuarios = leitor.readLine().split(",");  // Converte a string de contatos em um vetor.
					preencherListaUsuarios(usuarios);  // envia o vetor com os contatos para serem adicionados à interface.

				}else if(mensagem.equals(Comandos.LOGIN)) {  // Verifica se a mensagem é um pedido para o usuário fazer login.
					System.out.println("Servidor solicitou login");
					String nomeUsuario = JOptionPane.showInputDialog("Qual o seu nome?");  // Abre um popup de input.
					escritor.println(nomeUsuario);  // Envia o nome do usuário para o servidor.
					lblChatBoasvindas.setText("Bem Vindo " + nomeUsuario);  // Personaliza a label de boas vindas.
					System.out.println("Label do chat alterada");
					frmMensageiro.setTitle("Mensageiro S2 Olá " + nomeUsuario);
					System.out.println("Título da janela alterado");

				}else if(mensagem.equals(Comandos.LOGIN_EM_USO)){  // Verifica se o login foi negado.
					System.out.println("Usuário inseriu um nome que já está em uso");
					JOptionPane.showMessageDialog(Cliente.this, "O nome de usuário já está em uso");

				}else if(mensagem.equals(Comandos.LOGIN_EMPTY)) {
					System.out.println("Usuário não digitou um nome para login");
					JOptionPane.showMessageDialog(Cliente.this, "Digite um nome de usuário para continuar");

				}else if(mensagem.equals(Comandos.LOGIN_NULL)) {
					System.out.println("Usuário cancelou o login");
					escritor.println(Comandos.SAIR);
					System.out.println("Encerrando aplicativo");
					System.exit(0);

				}else if(mensagem.equals(Comandos.LOGIN_ACEITO)) {  // Verifica se o login foi aceito.
					System.out.println("Login realizado com sucesso");
					pedirListaUsuarios();  // Invoca o método que solicita que a lista de contatos seja enviada.

				}else {
					txtrVisor.append(mensagem);  // Exibe a mensagem recebida na tela.
					System.out.println("Exibindo mensagem recebida do servidor");
					txtrVisor.append("\n");  // Quebra de linha.
					txtrVisor.setCaretPosition(txtrVisor.getDocument().getLength());  //Ajusta a posição do cart de inserção de texto
				}
			}
		}catch(IOException e) {
			System.out.println("Não foi possível ler a mensagem do servidor");
			e.printStackTrace();
		}

	}


	public static void main(String[] args) {
		Cliente cliente = new Cliente();  // Cria uma nova instância do cliente
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					cliente.frmMensageiro.setVisible(true);
					System.out.println("Exibindo interface");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		cliente.estabelecerConexao();  // Estabelece conexão da instancia com o servidor.
		System.out.println("Conexão estabelecida");
		cliente.gerenciadorDeInputs();  // Gerencia Inputs do usuário e envia para o servidor.
		cliente.iniciarLeitor();  // Genrencia outputs do servidor.
	}

}
