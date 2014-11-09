package com.dbtool.queueingpetrinet;

public class TokenColour extends Colour {
	
	public TokenColour(String name) {
		super(name);
	}
	
	@Override
	public Object accept(QPNVisitor visitor) {
		return visitor.visit(this);
	}
	

}
