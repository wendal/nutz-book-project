package org.snaker.nutz;

import java.util.Map;

import javax.el.ExpressionFactory;

import org.apache.el.ExpressionFactoryImpl;
import org.nutz.el.El;
import org.nutz.lang.Lang;
import org.snaker.engine.Expression;

public class NutzExpression implements Expression {
	
	ExpressionFactory factory = new ExpressionFactoryImpl();
	
	@SuppressWarnings("unchecked")
	public <T> T eval(Class<T> T, String expr, Map<String, Object> args) {
		return (T)El.eval(Lang.context(args), expr);
	}
}