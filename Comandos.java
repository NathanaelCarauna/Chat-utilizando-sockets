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
 */

public class Comandos {

	public static final String LISTAR_CONTATOS = "//LISTAR_CONTATOS";
	public static final String SAIR = "//SAIR";
	public static final String MENSAGEM = "//MSG";
	public static final String LOGIN = "//LOGIN";
	public static final Object LOGIN_ACEITO = "//LOGIN_ACEITO";
	public static final String LOGIN_NULL = "//LOGIN_NEGADO_NULL";
	public static final String LOGIN_EMPTY = "//LOGIN_EMPTY";
	public static final String LOGIN_EM_USO = "//LOGIN_EM_USO";

}
