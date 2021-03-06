package krasa.translatorGenerator.assembler;

import com.intellij.openapi.editor.Document;
import com.intellij.psi.*;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.psi.util.PsiTreeUtil;

import krasa.translatorGenerator.Context;
import krasa.translatorGenerator.PsiFacade;
import krasa.translatorGenerator.generator.SetterCallsGenerator;

/**
 * @author Vojtech Krasa
 */
public class SetterCallsAssembler extends Assembler {

	private final PsiLocalVariable localVariable;
	private final PsiFacade psiFacade;
	private final Context context;
	private PsiElementFactory elementFactory;

	public SetterCallsAssembler(PsiLocalVariable variable, PsiFacade psiFacade, Context context) {
		super(psiFacade, context);
		this.localVariable = variable;
		this.psiFacade = psiFacade;
		this.context = context;
		elementFactory = JavaPsiFacade.getElementFactory(psiFacade.getProject());
	}

	public void generateSetterCalls() {
		SetterCallsGenerator generator = new SetterCallsGenerator(localVariable, context);
		PsiDeclarationStatement statement = PsiTreeUtil.getTopmostParentOfType(localVariable, PsiDeclarationStatement.class);

		StringBuilder sb = new StringBuilder();
		for (String s1 : generator.generateSetterCalls()) {
			sb.append(s1);
		}

		PsiDocumentManager instance = PsiDocumentManager.getInstance(psiFacade.getProject());
		Document document = instance.getDocument(localVariable.getContainingFile());
		if (document == null) {
			return;
		}
		int i = statement.getNextSibling().getTextOffset();
		document.replaceString(i, i, sb);
		instance.commitDocument(document);

		PsiFile psiFile = instance.getPsiFile(document);
		if (psiFile != null) {
			CodeStyleManager.getInstance(project).reformat(psiFile);
		}
	}

}
