package br.uaijug.interpreter;

import com.thedeanda.lorem.Lorem;
import com.thedeanda.lorem.LoremIpsum;

public class GeradorAleatorioDeLogs {

	private static String PADRAO_LOG = "{ \"data\": \"%s\" ,"
			+ " \"hora\": \"%s\" ,"
			+ " \"url\" : \"%s\" ,"
			+ " \"ip\": \"%s\" , "
			+ "\"idUsuario\": \"%s\" ,"
			+ " \"idAplicacao\": \"%s\" ,"
			+ " \"tempo\": \"%s\" ,"
			+ "  \"parametros\":  { %s }}";

	public static void main(String[] args) {


		for (int i = 0 ; i < 5; i ++) {

			String x = String.format(PADRAO_LOG,
				generateRandomDateString(06, 2018),
				generateRandomHoraString(),
				generateRandomUrl(),
				generateRandomIpv4(),
				generateRandomId(),
				generateRandomId(),
				generateRandomMillis(),
				generateRandomParameters());

			System.out.println(x);
		}

	}
	
	public static String generateRandomLogWithIpRepetitionAndUsers (Integer lines) {
		
		if (lines == null || lines == 0) return "";
		
		Integer range = (Integer) lines / 3;
		
		String ips [] = new String [range];
		
		String users [] = new String [range];
		
		for (int i = 0; i < range; i++) {
			ips[i] = generateRandomIpv4();
			users[i] = generateRandomId();
		}
		
		
		
		String log = "";
		for (int i = 0 ; i < lines; i ++) {
			
			String ipUtilizado = ips[i % range];
			String userUtilizado = users[i % range];
			
			String x = String.format(PADRAO_LOG,
				generateRandomDateString(06, 2018),
				generateRandomHoraString(),
				generateRandomUrl(),
				ipUtilizado,
				userUtilizado,
				generateRandomId(),
				generateRandomMillis(),
				generateRandomParameters());
			
			log = log + "\n" + x;
		}
		
		return log;
	}
	
	public static String generateRandomLog (Integer lines) {
		
		if (lines == null || lines == 0) return "";
		
		String log = "";
		for (int i = 0 ; i < 5; i ++) {

			String x = String.format(PADRAO_LOG,
				generateRandomDateString(06, 2018),
				generateRandomHoraString(),
				generateRandomUrl(),
				generateRandomIpv4(),
				generateRandomId(),
				generateRandomId(),
				generateRandomMillis(),
				generateRandomParameters());
			
			log = log + "\n" + x;
		}
		
		return log;
	}

	private static String generateRandomDateString (Integer mes , Integer ano) {

		Double dia = (Math.random() * 100) % 30;
		return dia.intValue() + "/" + mes +"/" + ano;
	}

	private static String generateRandomHoraString () {

		Double hora = (Math.random() * 100) % 24;
		Double min = (Math.random() * 100) % 60;
		Double seg = (Math.random() * 100) % 60;

		return hora.intValue() + ":" + min.intValue() + ":" + seg.intValue();

	}

	private static String generateRandomUrl () {
		Lorem lorem = LoremIpsum.getInstance();

		return "/" + lorem.getWords(1) + "/" + lorem.getWords(1);
	}


	private static String generateRandomIpv4() {
		Double ip1 = (Math.random() * 100) % 999;
		Double ip2 = (Math.random() * 100) % 999;
		Double ip3 = (Math.random() * 100) % 999;
		Double ip4 = (Math.random() * 100) % 999;

		return ip1.intValue() + "." + ip2.intValue() + "." + ip3.intValue() + "." + ip4.intValue();
	}

	private static String generateRandomId (){
		 Double id = (Math.random() * 10000) % 99999;
		 return "" + id.intValue();
	}

	private static String generateRandomMillis (){
		Double millis  = (Math.random() * 100) % 999;

		return "" + millis.intValue();
	}

	private static String generateRandomParameters () {
		Double numPar = (Math.random() * 100) % 15;

		String parametros = "";

		for (int i = 0 ; i < numPar.intValue(); i++) {
			if (i > 0 ) parametros = parametros + " , ";
			parametros = parametros + " \"parm"+i+"\":"+ "\"val-"+i+"\"";
		}

		return parametros;
	}
}
