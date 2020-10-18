package antipattern.detection.run;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.*;

import java.util.Iterator;
import java.util.List;

import core.visitor.common.EmptyCatchVisitor;
import java.io.*;

public class Application implements IApplication 
{
	int hashcode_counter = 0, useless_counter = 0, catch_counter = 0, project_counter = 0;
	
	@Override
	public Object start(IApplicationContext context) throws Exception 
	{

		long startTime = System.nanoTime();
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IProject[] projects = root.getProjects();
		
		for (IProject project : projects) 
		{

			if (project.isNatureEnabled("org.eclipse.jdt.core.javanature")) 
			{
				System.out.println("\n[0] Analyzing project name : " + project.getName());
				log_output(project.getName()+"\n", "projects.txt");
				analyzeProject(project);
				project_counter = project_counter+1;
			}
		}
		System.out.println("[0] Done.");
		long endTime = System.nanoTime();
		long duration = (endTime - startTime);
		System.out.println("[0] It took " + duration / 1000000 / 1000 + " seconds");
		System.out.println("[0] It analyze :" + project_counter + " projects");
		System.out.println("[0] hashcode_counter :"+hashcode_counter+";  useless_counter :"+useless_counter+";  catch_counter :"+catch_counter);
		return null;
	}

	@Override
	public void stop() 
	{
		System.out.println("[0] Stop");
	}
	 

	private static void log_output(String cache, String location)
    {
		try 
		{
			OutputStream out = new FileOutputStream(location,true) ;
			byte b[] = cache.getBytes() ; 
			out.write(b) ;
			out.close() ;
        } 
		catch (IOException e) 
		{
            e.printStackTrace();
        }
    }
	
	/**
	 * Perform bug 'hashCode but no equals' analysis on the Java project
	 * 
	 * @param types
	 */
	private int Bug_Pattern_hashcode(List types) 
	{
		int hashCode_flag = 0;
		String cache = "";
		for(Object obj : types) 
		{
			if(!(obj instanceof TypeDeclaration))
				continue;
			TypeDeclaration typeDec = (TypeDeclaration)obj;
			MethodDeclaration methodDec[] = typeDec.getMethods();
			for (MethodDeclaration method : methodDec)  
			{  
				SimpleName methodName = method.getName();
				if(methodName.toString().equals("hashCode"))
				{
					hashCode_flag = 1;
					cache = obj.toString();
				}
			}
			if(hashCode_flag == 1)
			{
				for (MethodDeclaration method : methodDec)  
				{  
					SimpleName methodName = method.getName();
					if(methodName.toString().equals("equals"))
						hashCode_flag = 0;
				}
			}
		}
		if(hashCode_flag == 1)
		{
			hashcode_counter = hashcode_counter + 1;
			log_output(cache, "log.txt");
		}
		return hashCode_flag;
	}
	
	/**
	 * Perform bug 'useless control flow' analysis on the Java project
	 * 
	 * @param stmt
	 */
	private int Bug_Pattern_useless_control_flow(Statement stmt) 
	{
		int useless_flag = 0;
		String cache = "";
		if(stmt instanceof IfStatement)
		{
			IfStatement ifstmt=(IfStatement) stmt;
			if(ifstmt.getThenStatement() instanceof Block)
			{
				List sub_list= ((Block)ifstmt.getThenStatement()).statements();
				if(sub_list.isEmpty())
					useless_flag = 1;
				else
				{
					Iterator iter= sub_list.iterator();
					while(iter.hasNext())
					{
						Statement sub_stmt=(Statement)iter.next();
						if(Bug_Pattern_useless_control_flow(sub_stmt)==1)
							useless_flag = 1;
					}
				}
			}
		}
		else if(stmt instanceof ForStatement)
		{
			ForStatement forstmt=(ForStatement) stmt;
			if(forstmt.getBody() instanceof Block)
			{
				List sub_list= ((Block)forstmt.getBody()).statements();
				if(sub_list.isEmpty())
					useless_flag = 1;
				else
				{
					Iterator iter= sub_list.iterator();
					while(iter.hasNext())
					{
						Statement sub_stmt=(Statement)iter.next();
						if(Bug_Pattern_useless_control_flow(sub_stmt)==1)
							useless_flag = 1;
					}
				}
			}
		}
		else if(stmt instanceof SwitchStatement)
		{
			SwitchStatement switchstmt=(SwitchStatement) stmt;
			int temp_flag = 0;
			for (Object obj : switchstmt.statements()) 
			{
				if(obj instanceof SwitchCase)
					if(temp_flag==0)
						temp_flag = 1;
					else
					{
						useless_flag = 1;
						cache = stmt.toString();
					}
						
				else
					temp_flag = 0;
			}
		}
		else if(stmt instanceof WhileStatement)
		{
			WhileStatement whilestmt=(WhileStatement) stmt;
			if(whilestmt.getBody() instanceof Block)
			{
				List sub_list= ((Block)whilestmt.getBody()).statements();
				if(sub_list.isEmpty())
					useless_flag = 1;
				else
				{
					Iterator iter= sub_list.iterator();
					while(iter.hasNext())
					{
						Statement sub_stmt=(Statement)iter.next();
						if(Bug_Pattern_useless_control_flow(sub_stmt)==1)
							useless_flag = 1;
					}
				}
			}
		}
		else if(stmt instanceof Block)
		{
			List sub_list = ((Block) stmt).statements();
			if(sub_list.isEmpty())
				useless_flag = 1;
			else
			{
				Iterator iter= sub_list.iterator();
				while(iter.hasNext())
				{
					Statement sub_stmt=(Statement)iter.next();
					if(Bug_Pattern_useless_control_flow(sub_stmt)==1)
						useless_flag = 1;
				}
			}
		}
		
		if(useless_flag == 1)
		{
			log_output(cache, "log.txt");
			useless_counter = useless_counter + 1;
		}
		return useless_flag;
	}
	
	/**
	 * Perform bug 'catch_inadequate' analysis on the Java project
	 * 
	 * @param stmt
	 */
	private int Bug_Pattern_catch_inadequate(Statement stmt) 
	{
		int inadequate_flag = 0;
		String cache = "";
		if(stmt instanceof TryStatement)
		{
			TryStatement tryStmt=(TryStatement)stmt;
			int temp_flag = 0;
			for (Object obj : tryStmt.catchClauses()) 
			{
				CatchClause catchStmt=(CatchClause)obj;
				for (Object ptr : tryStmt.catchClauses()) 
				{
					CatchClause ptrStmt=(CatchClause)ptr;
					if(ptrStmt.getBody().subtreeMatch(new ASTMatcher(), catchStmt.getBody()))
						temp_flag = temp_flag+1;
				}
				if(temp_flag > 1)
				{
					inadequate_flag = 1;
					cache = stmt.toString();
					break;
				}
				else
					temp_flag = 0;
			}
		}
		if(inadequate_flag == 1)
		{
			log_output(cache, "log.txt");
			catch_counter = catch_counter + 1;
		}
		return inadequate_flag;
	}
	
	/**
	 * Perform static analysis on the Java project
	 * 
	 * @param project
	 * @throws JavaModelException
	 */
	private void analyzeProject(IProject project) throws JavaModelException 
	{
		IPackageFragment[] packages = JavaCore.create(project).getPackageFragments();
		for (IPackageFragment mypackage : packages) 
		{
			if (mypackage.getKind() == IPackageFragmentRoot.K_SOURCE) 
			{
				if (mypackage.getElementName().toLowerCase().contains("test")) {
					continue;
				}
				analyze(mypackage, project.getName());
			}
		}
	}

	/**
	 * Analyze data usage of each Spring entry function
	 * 
	 * @param mypackage
	 * @throws JavaModelException
	 */
	private void analyze(IPackageFragment mypackage, String project_name) throws JavaModelException 
	{
		for (ICompilationUnit unit : mypackage.getCompilationUnits()) 
		{
			if (unit.getElementName().contains("test")
					|| (unit.getElementName().contains("IT") && !unit.getElementName().contains("ITenant")
							|| (unit.getElementName().contains("Test"))))
				continue;

			CompilationUnit parsedUnit = parse(unit, project_name);
			EmptyCatchVisitor exVisitor = new EmptyCatchVisitor(mypackage, unit, parsedUnit);
			parsedUnit.accept(exVisitor);
		}

	}

	/**
	 * Reads a ICompilationUnit and creates the AST DOM for manipulating the Java
	 * source file
	 * 
	 * @param unit
	 * @return
	 */

	private CompilationUnit parse(ICompilationUnit unit,String project_name) 
	{
		
		// Initialize ASTParser
		ASTParser parser = ASTParser.newParser(AST.JLS4);
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		parser.setSource(unit);
		parser.setResolveBindings(true);
		CompilationUnit result = (CompilationUnit) parser.createAST(null);
		
		// Parse Import
		List importList = result.imports();  
		System.out.println("[1] import:");
		for(Object obj : importList) {
			ImportDeclaration importDec = (ImportDeclaration)obj;
			System.out.println(importDec.getName());
		}
		
		// Parse class_name
		List types = result.types();
		
		// bug_pattern_func1
		if(Bug_Pattern_hashcode(types)==1)
			System.out.println("[*****ERROR*****] hashcode no equals!");
		
		System.out.println("[2] class name:");
		for(Object obj : types) 
		{
			
			// Parse class_properties
			if(!(obj instanceof TypeDeclaration))
				continue;
			TypeDeclaration typeDec = (TypeDeclaration)obj;
			System.out.println(typeDec.getName());
			FieldDeclaration fieldDec[]=typeDec.getFields();
			System.out.println("[3] class properties:");
			for(FieldDeclaration field: fieldDec)
			{
				System.out.println("[3] fragment:"+field.fragments());
				System.out.println("[3] type:"+field.getType());
			}
			
			// Parse method
			MethodDeclaration methodDec[] = typeDec.getMethods();  
			System.out.println("[4] Method:");  
			for (MethodDeclaration method : methodDec)  
			{  
				//get method name
				SimpleName methodName=method.getName();
				System.out.println("[4] method name:"+methodName);
				
				//get method parameters
				List param=method.parameters();
				System.out.println("[4] method parameters:"+param);
				
				//get method return type
				Type returnType=method.getReturnType2();
				System.out.println("[4] method return type:"+returnType);
				
				//get method body
				Block body=method.getBody();
				if(body == null)
					break;
				List statements=body.statements();   //get the statements of the method body
				Iterator iter=statements.iterator();
				while(iter.hasNext())
				{
					//get each statement
					Statement stmt=(Statement)iter.next();
					
					// bug_pattern_func2
					if(Bug_Pattern_useless_control_flow(stmt) == 1)
						System.out.println("[*****ERROR*****]: useless control flow!");
					
					// bug_pattern_func3
					if(Bug_Pattern_catch_inadequate(stmt)  == 1)
					{
						System.out.println("[*****ERROR*****]: catch inadequate!");
						log_output(project_name + "\n", "info.txt");
					}
					
				}
			}
		}
		
		return result;
	}
}
