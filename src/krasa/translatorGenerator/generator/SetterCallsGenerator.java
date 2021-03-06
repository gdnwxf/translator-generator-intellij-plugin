package krasa.translatorGenerator.generator;

import java.util.ArrayList;
import java.util.List;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiLocalVariable;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.impl.source.PsiClassReferenceType;

import krasa.translatorGenerator.Context;
import krasa.translatorGenerator.Utils;

/**
 * @author Vojtech Krasa
 */
public class SetterCallsGenerator extends MethodCodeGenerator {

	private static final Logger LOG = Logger.getInstance("#" + MethodCodeGenerator.class.getName());

	private PsiLocalVariable identifier;
	private Context context;

	public SetterCallsGenerator(PsiLocalVariable identifier, Context context) {
		this.identifier = identifier;
		this.context = context;
	}

	public List<String> generateSetterCalls() {
		PsiClassReferenceType type = (PsiClassReferenceType) identifier.getType();
		PsiClass resolve = type.resolve();
		PsiField[] allFields = resolve.getAllFields();
		return generateCallsForFields(allFields, identifier.getName());
	}

	private List<String> generateCallsForFields(PsiField[] fields, String inputVariable) {
		List<String> strings = new ArrayList<String>();
		for (int i = 0; i < fields.length; i++) {
			String s = "";
			PsiField field = fields[i];
			if (Utils.isStatic(field)) {
				continue;
			}
			PsiMethod setter = Utils.setter(field);
			PsiMethod getter = Utils.getter(field);

			if (setter == null && getter != null) {
				s += Utils.newLineTodo();
				s += "\n" + inputVariable + "." + getter.getName() + "();";
			} else if (setter != null) {
				s += "\n" + inputVariable + "." + setter.getName() + "(null);";
			} else {
				s += "\n" + inputVariable + "." + field.getName() + "= null;";
			}
			strings.add(s);
		}
		return strings;
	}
}
