package interpreter;

import models.*;
import parser.SimpleParser;
import util.Node;
import util.OperationCodeGeneration;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class SimpleStmtBlockInterp extends SimpleStmt {

	public List<SimpleStmt> children;

	/**
	 * Creates a new block
	 * @param children: the list of direct children elements of the block
	 */
	public SimpleStmtBlockInterp(List<SimpleStmt> children) {
		this.children = children;
	}

	@Override
	public List<SemanticError> checkSemantics(EnvironmentVariables ev, EnvironmentFunctions ef) {
		return null;
	}

	@Override
	public List<Node> codeGeneration(EnvironmentVariablesWithOffset ev, EnvironmentFunctionsWithLabel ef, OperationCodeGeneration oCgen) {
		List<Node> codeBlock = new ArrayList<>();

		List<String> variablesDeclared;

		ev.openScope();
		ef.openScope();

		oCgen.increaseNestingLevel();

		variablesDeclared = visitBlockAndGetDeclaration(children, ev);

		for(int i = variablesDeclared.size()-1; i >= 0; i--){
			codeBlock.add(oCgen.addi("sp", "sp", "-1"));
		}

		codeBlock.addAll(oCgen.push("fp"));
		codeBlock.add(oCgen.move("fp", "sp"));

		//list for saving children statements
		List<Node> statementCode = new ArrayList<>();

		//visit each children
		if(children!=null)
			for(SimpleStmt el:children)
				statementCode.addAll(el.codeGeneration(ev, ef, oCgen));

		codeBlock.addAll(statementCode);

		ef.closeScope();
		ev.closeScope();
		oCgen.decreaseNestingLevel();

		codeBlock.add(oCgen.top("fp"));
		codeBlock.add(oCgen.pop());

		for (String var: variablesDeclared) {
			codeBlock.add(oCgen.pop());
		}

		int i = 0;
		for (Node node: codeBlock) {
			System.out.println("instr: " + (i++) + " " + node.getInstr() + " arg1: "+ node.getArg1()+" offset: "+node.getOffset()+" arg2: "+ node.getArg2()+" arg3: " + node.getArg3());
		}
		System.out.println("\n\n\n\n");

		return codeBlock;
	}

	public List<Node> codeGenerationDeclaration(EnvironmentVariablesWithOffset ev, EnvironmentFunctionsWithLabel ef, OperationCodeGeneration oCgen) {
		List<Node> codeBlock = new ArrayList<>();

		//list for saving children statements
		List<Node> statementCode = new ArrayList<>();

		//visit each children
		if(children!=null)
			for(SimpleStmt el:children)
				statementCode.addAll(el.codeGeneration(ev, ef, oCgen));

		codeBlock.addAll(statementCode);

		return codeBlock;
	}

	private List<String> visitBlockAndGetDeclaration(List<SimpleStmt> children, EnvironmentVariablesWithOffset ev) {
		List<String> variablesDeclared = new ArrayList<>();

		for (SimpleStmt child : children) {
			if (child.getClass() == SimpleStmtDeclaration.class){
				SimpleStmtDeclaration declaration = (SimpleStmtDeclaration) child;
				if (declaration.getType() != null){
					variablesDeclared.add(declaration.getID());
					ev.varDeclaration(declaration.getID());
				}
			}
		}

		return variablesDeclared;
	}

}