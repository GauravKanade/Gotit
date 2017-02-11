package com.gotit.questions.util;

public class GotitConstants {
	public static String QUESTION_MAPPING = "{\"question\": { \"properties\": {\"questionBody\": {\"type\": \"string\",\"analyzer\": \"Partial\",\"search_analyzer\": \"Original\" },\"category\": {\"type\": \"string\",\"analyzer\":\"Phrase\",\"search_analyzer\": \"Original\" } } } }";
	public static String USER_MAPPING = "{\"user\": {\"properties\": {\"emailId\": {\"type\": \"string\",\"analyzer\": \"Partial\",\"search_analyzer\": \"Original\" } } } }";
	public static String SETTINGS = "{\"analysis\": {\"filter\": {\"original\": {\"type\": \"lowercase\"},\"phrase\": {\"type\": \"shingle\",\"max_shingle_size\": 20},\"partial\": {\"type\": \"ngram\",\"min_gram\": 1,\"max_gram\": 30 },\"word\": {\"type\": \"word_delimiter\",\"split_on_numerics\": false,\"preserve_original\": true } }, \"analyzer\": {\"Partial\": {\"type\": \"custom\",\"tokenizer\": \"keyword\",\"filter\": [\"partial\",\"original\" ] }, \"Phrase\": {\"type\": \"custom\", \"tokenizer\": \"standard\",\"filter\": [\"phrase\",\"word\",\"original\" ] }, \"Original\": { \"type\": \"custom\", \"tokenizer\": \"keyword\", \"filter\": [ \"original\" ] } } } }";
	public static int NUMBER_OF_QUESTIONS_PER_SYNC = 20;
}
