package net.wendal.nutzbook.util;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.nutz.mvc.NutConfigException;

import freemarker.core.Environment;
import freemarker.template.ObjectWrapper;
import freemarker.template.TemplateBooleanModel;
import freemarker.template.TemplateDateModel;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateNumberModel;
import freemarker.template.TemplateScalarModel;
import freemarker.template.utility.DeepUnwrap;

/**
 * Freemarker标签工具类
 */
public abstract class DirectiveUtils {
	/**
	 * 输出参数：对象数据
	 */
	public static final String OUT_BEAN = "tag_bean";
	/**
	 * 输出参数：列表数据
	 */
	public static final String OUT_LIST = "tag_list";
	/**
	 * 输出参数：分页数据
	 */
	public static final String OUT_PAGINATION = "tag_pagination";
	/**
	 * 参数：是否调用模板。
	 */
	public static final String PARAM_TPL = "tpl";
	/**
	 * 参数：次级模板名称
	 */
	public static final String PARAM_TPL_SUB = "tplSub";

	/**
	 * 将params的值复制到variable中
	 * 
	 * @param env
	 * @param params
	 * @return 原Variable中的值
	 * @throws TemplateException
	 */
	public static Map<String, TemplateModel> addParamsToVariable(Environment env, Map<String, TemplateModel> params) throws TemplateException {
		Map<String, TemplateModel> origMap = new HashMap<String, TemplateModel>();
		if (params.size() <= 0) {
			return origMap;
		}
		Set<Map.Entry<String, TemplateModel>> entrySet = params.entrySet();
		String key;
		TemplateModel value;
		for (Map.Entry<String, TemplateModel> entry : entrySet) {
			key = entry.getKey();
			value = env.getVariable(key);
			if (value != null) {
				origMap.put(key, value);
			}
			env.setVariable(key, entry.getValue());
		}
		return origMap;
	}

	/**
	 * 将variable中的params值移除
	 * 
	 * @param env
	 * @param params
	 * @param origMap
	 * @throws TemplateException
	 */
	public static void removeParamsFromVariable(Environment env, Map<String, TemplateModel> params, Map<String, TemplateModel> origMap) throws TemplateException {
		if (params.size() <= 0) {
			return;
		}
		for (String key : params.keySet()) {
			env.setVariable(key, origMap.get(key));
		}
	}

	public static String getString(String name, Map<String, TemplateModel> params) throws TemplateException {
		TemplateModel model = params.get(name);
		if (model == null) {
			return null;
		}
		if (model instanceof TemplateScalarModel) {
			return ((TemplateScalarModel) model).getAsString();
		} else if ((model instanceof TemplateNumberModel)) {
			return ((TemplateNumberModel) model).getAsNumber().toString();
		} else {
			throw new NutConfigException("The \"" + name + "\" parameter must be a string.");
		}
	}

	public static Long getLong(String name, Map<String, TemplateModel> params) throws TemplateException {
		TemplateModel model = params.get(name);
		if (model == null) {
			return null;
		}
		if (model instanceof TemplateScalarModel) {
			String s = ((TemplateScalarModel) model).getAsString();
			if (StringUtils.isBlank(s)) {
				return null;
			}
			try {
				return Long.parseLong(s);
			} catch (NumberFormatException e) {
				throw new NutConfigException("The \"" + name + "\" parameter must be a string.");
			}
		} else if (model instanceof TemplateNumberModel) {
			return ((TemplateNumberModel) model).getAsNumber().longValue();
		} else {
			throw new NutConfigException("The \"" + name + "\" parameter must be a string.");
		}
	}

	public static Integer getInt(String name, Map<String, TemplateModel> params) throws TemplateException {
		TemplateModel model = params.get(name);
		if (model == null) {
			return -1;
		}
		if (model instanceof TemplateScalarModel) {
			String s = ((TemplateScalarModel) model).getAsString();
			return NumberUtils.toInt(s, -1);
		} else if (model instanceof TemplateNumberModel) {
			return ((TemplateNumberModel) model).getAsNumber().intValue();
		} else {
			throw new NutConfigException("The \"" + name + "\" parameter must be a string.");
		}
	}

	public static Integer[] getIntArray(String name, Map<String, TemplateModel> params) throws TemplateException {
		String str = DirectiveUtils.getString(name, params);
		if (StringUtils.isBlank(str)) {
			return null;
		}
		String[] arr = StringUtils.split(str, ',');
		Integer[] ids = new Integer[arr.length];
		int i = 0;
		try {
			for (String s : arr) {
				ids[i++] = Integer.valueOf(s);
			}
			return ids;
		} catch (NumberFormatException e) {
			throw new NutConfigException("The \"" + name + "\" parameter must be a number split by ','", e);
		}
	}

	public static Boolean getBool(String name, Map<String, TemplateModel> params) throws TemplateException {
		TemplateModel model = params.get(name);
		if (model == null) {
			return false;
		}
		if (model instanceof TemplateBooleanModel) {
			return ((TemplateBooleanModel) model).getAsBoolean();
		} else if (model instanceof TemplateNumberModel) {
			return !(((TemplateNumberModel) model).getAsNumber().intValue() == 0);
		} else if (model instanceof TemplateScalarModel) {
			String s = ((TemplateScalarModel) model).getAsString();
			// 空串应该返回null还是true呢？
			if (!StringUtils.isBlank(s)) {
				return !(s.equals("0") || s.equalsIgnoreCase("false") || s.equalsIgnoreCase("f"));
			} else {
				return false;
			}
		} else {
			throw new NutConfigException("The \"" + name + "\" parameter must be a boolean.");
		}
	}

	public static Date getDate(String name, Map<String, TemplateModel> params) throws TemplateException {
		TemplateModel model = params.get(name);
		if (model == null) {
			return null;
		}
		if (model instanceof TemplateDateModel) {
			return ((TemplateDateModel) model).getAsDate();
		} else if (model instanceof TemplateScalarModel) {
			DateTypeEditor editor = new DateTypeEditor();
			editor.setAsText(((TemplateScalarModel) model).getAsString());
			return (Date) editor.getValue();
		} else {
			throw new NutConfigException("The \"" + name + "\" parameter must be a date.");
		}
	}

	/**
	 * 模板调用类型
	 */
	public enum InvokeType {
		body, custom, sysDefined, userDefined
	};

	/**
	 * 是否调用模板
	 * 
	 * 0：不调用，使用标签的body；1：调用自定义模板；2：调用系统预定义模板；3：调用用户预定义模板。默认：0。
	 * 
	 * @param params
	 * @return
	 * @throws TemplateException
	 */
	public static InvokeType getInvokeType(Map<String, TemplateModel> params) throws TemplateException {
		String tpl = getString(PARAM_TPL, params);
		if ("3".equals(tpl)) {
			return InvokeType.userDefined;
		} else if ("2".equals(tpl)) {
			return InvokeType.sysDefined;
		} else if ("1".equals(tpl)) {
			return InvokeType.custom;
		} else {
			return InvokeType.body;
		}
	}

	public static Object getObject(String paramString, Map<String, TemplateModel> paramMap) throws TemplateModelException {
		TemplateModel localTemplateModel = (TemplateModel) paramMap.get(paramString);
		if (localTemplateModel == null)
			return null;
		try {
			return DeepUnwrap.unwrap(localTemplateModel);
		} catch (TemplateModelException localTemplateModelException) {
			throw new TemplateModelException("The \"" + paramString + "\" parameter " + "must be a object.");
		}
	}

	public static TemplateModel getVariable(String name, Environment env) throws TemplateModelException {
		return env.getVariable(name);
	}

	public static void setVariables(Map<String, Object> variables, Environment env) throws TemplateModelException {
		Iterator<Entry<String, Object>> localIterator = variables.entrySet().iterator();
		while (localIterator.hasNext()) {
			Entry<String, Object> localEntry = localIterator.next();
			String str = localEntry.getKey();
			Object localObject = localEntry.getValue();
			if (localObject instanceof TemplateModel)
				env.setVariable(str, (TemplateModel) localObject);
			else
				env.setVariable(str, ObjectWrapper.BEANS_WRAPPER.wrap(localObject));
		}
	}
	
	public static void setVariables(Map<String, Object> paramMap, Environment paramEnvironment, TemplateDirectiveBody paramTemplateDirectiveBody) throws TemplateException, IOException {
		Map<String, Object> localHashMap = new HashMap<String, Object>();
		Iterator<String> localIterator = paramMap.keySet().iterator();
		while (localIterator.hasNext()) {
			String str = localIterator.next();
			TemplateModel localTemplateModel = DirectiveUtils.getVariable(str, paramEnvironment);
			localHashMap.put(str, localTemplateModel);
		}
		DirectiveUtils.setVariables(paramMap, paramEnvironment);
		paramTemplateDirectiveBody.render(paramEnvironment.getOut());
		DirectiveUtils.setVariables(localHashMap, paramEnvironment);
	}
}
