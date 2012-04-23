package parser;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public
@Data
class Attribute implements IAttribute {
	private final String name;
	private String value;
}
