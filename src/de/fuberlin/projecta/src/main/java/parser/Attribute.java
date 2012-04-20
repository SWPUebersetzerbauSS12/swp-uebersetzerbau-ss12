package parser;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public @Data class Attribute {
	private final String name;
	private String value;
	
}
