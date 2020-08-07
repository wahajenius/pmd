/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.errorprone;

import java.io.InputStream;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryExpression;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryPrefix;
import net.sourceforge.pmd.lang.java.ast.ASTStatementExpression;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.lang.java.symboltable.JavaNameOccurrence;
import net.sourceforge.pmd.lang.java.types.TypeTestUtil;
import net.sourceforge.pmd.lang.symboltable.NameOccurrence;

public class CheckSkipResultRule extends AbstractJavaRule {

    @Override
    public Object visit(ASTVariableDeclaratorId node, Object data) {
        if (!TypeTestUtil.isA(InputStream.class, node.getTypeNode())) {
            return data;
        }
        for (NameOccurrence occ : node.oldGetUsages()) {
            JavaNameOccurrence jocc = (JavaNameOccurrence) occ;
            NameOccurrence qualifier = jocc.getNameForWhichThisIsAQualifier();
            if (qualifier != null && "skip".equals(qualifier.getImage())) {
                Node loc = jocc.getLocation();
                if (loc != null) {
                    ASTPrimaryExpression exp = loc.getFirstParentOfType(ASTPrimaryExpression.class);
                    while (exp != null) {
                        if (exp.getParent() instanceof ASTStatementExpression) {
                            // if exp is in a bare statement,
                            // the returned value is not used
                            addViolation(data, occ.getLocation());
                            break;
                        } else if (exp.getParent() instanceof ASTExpression
                                && exp.getParent().getParent() instanceof ASTPrimaryPrefix) {
                            // if exp is enclosed in a pair of parenthesis
                            // let's have a look at the enclosing expression
                            // we'll see if it's in a bare statement
                            exp = exp.getFirstParentOfType(ASTPrimaryExpression.class);
                        } else {
                            // if exp is neither in a bare statement
                            // or between a pair of parentheses,
                            // it's in some other kind of statement
                            // or assignment so the returned value is used
                            break;
                        }
                    }
                }
            }
        }
        return data;
    }
}
