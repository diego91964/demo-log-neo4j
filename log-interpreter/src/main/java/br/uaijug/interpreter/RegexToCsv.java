package br.uaijug.interpreter;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fasterxml.jackson.core.JsonParser.Feature;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class RegexToCsv {
	
	

	public static void main(String[] args) {
		
		
		String csv = "data$$hora$$idProjeto$$idAvaliacao$$idUsuario$$idAvaliacaoQuestao$$nota$$justificativa";
		csv += "\n" + tratarLog("/home/diego/dev/logs/sg.log.14-04-2018");
		csv += "\n" + tratarLog("/home/diego/dev/logs/sg.log.15-04-2018");
		csv += "\n" + tratarLog("/home/diego/dev/logs/sg.log.16-04-2018");
		csv += "\n" + tratarLog("/home/diego/dev/logs/sg.log.17-04-2018");
		csv += "\n" + tratarLog("/home/diego/dev/logs/sg.log.18-04-2018");
		csv += "\n" + tratarLog("/home/diego/dev/logs/sg.log.19-04-2018");
		csv += "\n" + tratarLog("/home/diego/dev/logs/sg.log.20-04-2018");
		csv += "\n" + tratarLog("/home/diego/dev/logs/sg.log.21-04-2018");
		csv += "\n" + tratarLog("/home/diego/dev/logs/sg.log.22-04-2018");
		csv += "\n" + tratarLog("/home/diego/dev/logs/sg.log.23-04-2018");
		csv += "\n" + tratarLog("/home/diego/dev/logs/sg.log.24-04-2018");
		csv += "\n" + tratarLog("/home/diego/dev/logs/sg.log.25-04-2018");
		csv += "\n" + tratarLog("/home/diego/dev/logs/sg.log.26-04-2018");
		csv += "\n" + tratarLog("/home/diego/dev/logs/sg.log.27-04-2018");
		
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter("/home/diego/dev/logs/csvfinal"));
			writer.write(csv);
		    writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static String tratarLog (String fileName){
		
		String arquivoCSV = "";
		
		try {
			System.out.println("tratando arquivo: "+ fileName);
			String text = new String(Files.readAllBytes(Paths.get(fileName)), StandardCharsets.UTF_8);
			
			if (fileName.equals("/home/diego/dev/logs/sg.log.20-04-2018")){
				text = text.replaceAll("Feijo, A. M. L. C., \" , \" Goto, T. A. (2016).", "Feijo A M L C Goto T. A. (2016).");
				text = text.replaceAll("Pesquisa, 32(4), 1-9.", "Pesquisa 32(4) 19.");
				text = text.replaceAll("\n", " ");
			}
			
			//final String regex = "\\{ \\\"data\\\":.*, \"hora\":.*, \"url\" :.* , \\\"parametros\":.*\" \\}";
			final String regex = "\\{ \"data\": .* \"url\" : \"/avaliacaoInternaProjeto/salvarAvaliacao\" ,.*\" \\}";

			final Pattern pattern = Pattern.compile(regex);
			final Matcher matcher = pattern.matcher(text);

			ObjectMapper objectMapper = new ObjectMapper();
			objectMapper.configure(Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
			objectMapper.configure(Feature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER, true);
			objectMapper.configure(Feature.ALLOW_MISSING_VALUES, true);
			objectMapper.configure(Feature.ALLOW_NUMERIC_LEADING_ZEROS, true);
			objectMapper.configure(Feature.ALLOW_NON_NUMERIC_NUMBERS, true);
			objectMapper.configure(Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
			
			
			
			while (matcher.find()) {
			    
			    TypeReference<HashMap<String,Object>> typeRef = new TypeReference<HashMap<String,Object>>() {};

			    HashMap<String,Object> o = objectMapper.readValue(matcher.group(0), typeRef);
			    
			    
			    if (o.get("url") != null && !o.get("url").toString().contains("/js/") &&
			    		!o.get("url").toString().contains("/images")){
			    
				    String idUsuario = o.get("idUsuario") == null ? "-1" : o.get("idUsuario").toString();
				    
				    String idAplicacao = o.get("idAplicacao") == null ? "-1" : o.get("idAplicacao").toString();
				    
				    HashMap<String,Object> parametros = (HashMap<String, Object>) o.get("parametros");
				    
				    TypeReference<List<HashMap<String,Object>>> typeRef2 = new TypeReference<List<HashMap<String,Object>>>() {};
				    
				    if (parametros != null && parametros.get("respostas") != null){
				    	
				    	String respostasString = parametros.get("respostas").toString();
					    respostasString = respostasString.replaceAll("\"", " ");
					    respostasString = respostasString.replaceAll("justificativa :", "justificativa : \"");
					    respostasString = respostasString.replaceAll(", nota", " \" , nota");
					    respostasString = respostasString.replaceAll("nota :  }", " nota :  \" \" }");
					    respostasString = respostasString.replaceAll("\n", " ");
					    
					    String idProjeto = " ";
					    String idAvaliacao = " " ;
					    
					    if (parametros.get("idProjeto") != null ){
					    	idProjeto = parametros.get("idProjeto").toString();
					    }
					    
					    if (parametros.get("idAvaliacao") != null ){
					    	idAvaliacao = parametros.get("idAvaliacao").toString();
					    }
					    
					    if (idProjeto != null && !idProjeto.equals(" ") && estaNaLista(idProjeto)){
					    	
					    	 List<HashMap<String,Object>> respostas = objectMapper.readValue(respostasString, typeRef2);
							    
							    String grupoDeDados = "";
							    
							    //idProjeto$$idUsuario$$idAvaliacaoQuestao$$nota$$justificativa
							    
							    for (int i = 0 ; i < respostas.size() ; i++){
							    	HashMap<String,Object> resposta = respostas.get(i);

							    	String linhaCSV = "";
							    	linhaCSV += 
							    			"'" + o.get("data")  + "'" + "$$" +
							    			"'" + o.get("hora")  + "'" + "$$" +
							    			idProjeto + "$$" +
							    			idAvaliacao + "$$" +
							    			idUsuario + "$$" +
							    			resposta.get("idAvaliacaoQuestao") + "$$" +
							    			resposta.get("nota") + "$$" +
							    			"'" + resposta.get("justificativa") + "'";
							    	
							    	grupoDeDados += "\n" + linhaCSV;
							    }
							  
							    arquivoCSV +=  grupoDeDados;
					    }
					   
					    
				    }
				    
			    }
			    
			}
			
			return arquivoCSV;
			 
			 
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	
	public static boolean estaNaLista (String idProjeto){
		
		String[] listaDeIdsComProblema = new String[]{"659","566","873","786","1107","598","646","1037","810","823","573","856","1125","1121","812","743","675","805","671","859","850","648","885","617","1178","1123","816","1035","1099","1096","605","709","987","808","1091","822","682","541","954","569","893","663","564","623","623","588","1013","770","632","659","711","744"};
		
		for (String idLista : listaDeIdsComProblema){
			if (idProjeto.equals(idLista))
				return true;
		}
		
		return false;
		
		
		
	}
}
