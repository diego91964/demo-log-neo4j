package br.uaijug.interpreter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.neo4j.driver.v1.AuthTokens;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.GraphDatabase;
import org.neo4j.driver.v1.Session;

import com.fasterxml.jackson.core.JsonParser.Feature;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;


public class RegexToString {
	
	public static final String QUERY_MERGE_IP = "MERGE (ip:IP {ip:'%s'})";

	public static final String QUERY_MERGE_URL = "MERGE (url:URL {url:'%s'})";
	
	public static final String QUERY_MERGE_USUARIO = "MERGE (usuario:Usuario {idUsuario : '%s'})";
	
	public static final String QUERY_MERGE_DATA = "MERGE (data: Data { data : '%s' })";
	
	public static final String QUERY_MERGE_HORA = "MERGE (hora: Hora { hora: '%s' } )";
	
	public static final String QUERY_MERGE_APLICACAO = "MERGE (aplicacao: Aplicacao { idAplicacao : '%s' } )";
	
	public static final String QUERY_REQUEST_1 = "" +
	
		" MATCH (ip:IP) , (url:URL), (usuario:Usuario) , (data: Data),  (hora: Hora), (aplicacao: Aplicacao) " +
		" WHERE ip.ip              = '%s' AND  " +
		"   url.url                = '%s' AND " +
		"   usuario.idUsuario      = '%s' AND " +
		"   data.data              = '%s' AND " +
		" 	hora.hora              = '%s' AND " +
		"   aplicacao.idAplicacao  = '%s' " +
		" CREATE (req : Requisicao { tempo : '%s'} ) ";
	
	public static final String QUERY_REQUEST_2 = "" + 	
		" CREATE (ip)          - [:ip_request] -> (req) " +
		" CREATE (url)         - [:url_request] -> (req) " +
		" CREATE (usuario)     - [:usuario_request] -> (req) " +
		" CREATE (data)        - [:data_request] -> (req) " +
		" CREATE (hora)        - [:hora_request] -> (req) " +
		" CREATE (aplicacao)   - [:aplicacao_request] -> (req) ";
	
	
	
	public static final String QUERY_RESPOSTA =  " CREATE ( param : Parametro { " + 
	" %s " +
	" } )  " +
	" CREATE (param) - [:resp_request] -> (req) ";
	
	public static void main(String[] args) {
	
		tratarLog(GeradorAleatorioDeLogs.generateRandomLogWithIpRepetitionAndUsers(10));
		
	}
	
	public static void tratarLog (String log){
		
		try {
			
			
			//final String regexBase = "{\\s\\\"data\\\":\\s.*\\/.*\\/.*\\s,\\s\\\"hora\\\":\\s.*,\\s\\\"url\\\"\\s:\\s.*,\\s\\\"ip\\\":\\s.*,\\s\\\"idUsuario\\\":\\s.*,\\s\\\"idAplicacao\\\":\\s.*,\\s\\\"tempo\\\":.*,\\s\\s\\\"parametros\\\":.*}}";
			final String regex = "\\{\\s\\\"data\\\":\\s.*\\/.*\\/.*\\s,\\s\\\"hora\\\":\\s.*,\\s\\\"url\\\"\\s:\\s.*,\\s\\\"ip\\\":\\s.*,\\s\\\"idUsuario\\\":\\s.*,\\s\\\"idAplicacao\\\":\\s.*,\\s\\\"tempo\\\":.*,\\s\\s\\\"parametros\\\":.*\\}}";

			final Pattern pattern = Pattern.compile(regex);
			final Matcher matcher = pattern.matcher(log);

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
			    
			    
			    if (o.get("url") != null){
			    
				    System.out.println(String.format(QUERY_MERGE_IP , o.get("ip")));
				    System.out.println(String.format(QUERY_MERGE_URL, o.get("url")));
				    
				    String idUsuario = o.get("idUsuario") == null ? "-1" : o.get("idUsuario").toString();
				    System.out.println(String.format(QUERY_MERGE_USUARIO, idUsuario));
				    
				    System.out.println(String.format(QUERY_MERGE_DATA , o.get("data")));
				    System.out.println(String.format(QUERY_MERGE_HORA , o.get("hora")));
				    
				    String idAplicacao = o.get("idAplicacao") == null ? "-1" : o.get("idAplicacao").toString();
				    System.out.println(String.format(QUERY_MERGE_APLICACAO , idAplicacao ));
			    	
				    HashMap<String,Object> parametros = (HashMap<String, Object>) o.get("parametros");
				    
				    TypeReference<List<HashMap<String,Object>>> typeRef2 = new TypeReference<List<HashMap<String,Object>>>() {};
				    
				    if (parametros != null && parametros.keySet() != null){
				    	
				    	ArrayList<String> listParameteres = new ArrayList<>();
				    	
				    	for (String key : parametros.keySet()) {
				    		listParameteres.add( key + ":" + "'"+parametros.get(key)+"'");
				    	}
				    	
				    	String parametrosString = "";
				    	for (int i = 0; i < listParameteres.size() ; i ++) {
				    		if (i > 0 ) parametrosString = parametrosString + " , ";
				    		parametrosString = parametrosString + listParameteres.get(i);
				    	}
					    
					    String queryGeral_1 = String.format(QUERY_REQUEST_1, 
					    		o.get("ip"),
					    		o.get("url"),
					    		idUsuario,
					    		o.get("data"),
					    		o.get("hora"),
					    		idAplicacao,
					    		o.get("tempo")
					    );
					    
					    String queryParametros = String.format(QUERY_RESPOSTA, parametrosString);
					   
					    String queryGeral = queryGeral_1 + queryParametros +  QUERY_REQUEST_2;
					    
					    System.out.println(queryGeral);
					    //session.run(queryGeral);
				    }
				    
			    }
			    
			}
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

