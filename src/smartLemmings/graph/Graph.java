package smartLemmings.graph;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

import javax.swing.JFileChooser;

import smartLemmings.qlearning.QStateLemmings;

public class Graph implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ArrayList<GraphNode> nodes;
	private Graph graph;
	private String path;
	
	public Graph(String pathIn, boolean load){
		path = pathIn;
		if(load){
			try {
				loadGraph();
			} catch (FileNotFoundException e) {
				System.out.println(e.getMessage());
			} catch (ClassNotFoundException e) {
				System.out.println(e.getMessage());
			} catch (IOException e) {
				System.out.println(e.getMessage());
			}
		} else {
			nodes=new ArrayList<GraphNode>();
		}
	}


	
	
	 public void saveGraph() throws IOException {
	        FileOutputStream f = new FileOutputStream(getSavePath());
	        ObjectOutputStream o = new ObjectOutputStream(f);
	        o.writeObject(this);
	        o.close();
	    }
	 
	 public void loadGraph() throws FileNotFoundException, IOException, ClassNotFoundException{
	        FileInputStream f = new FileInputStream(getLoadPath());
	        ObjectInputStream in = new ObjectInputStream(f);
	        this.graph = (Graph)in.readObject();
	        in.close();
	        nodes = this.graph.getNodes(); 
	    }
	 public ArrayList<GraphNode> getNodes(){
		 return nodes;
	 }
	 
	 public String getSavePath(){
	        JFileChooser ficChoix = new JFileChooser();
	        ficChoix.showSaveDialog(ficChoix);
	        return ficChoix.getSelectedFile().getPath();
	    }

	    public String getLoadPath(){
	        JFileChooser ficChoix = new JFileChooser();
	        ficChoix.showOpenDialog(ficChoix);
	        return ficChoix.getSelectedFile().getPath();
	    }
	
}
