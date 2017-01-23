package ru.savushkin.loader;

import com.jogamp.opengl.GL2;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;


public class ModelLoaderOBJ {
	
	public static GLModel LoadModel(String objPath, String mtlPath, GL2 gl)
	{
		GLModel model = null;
		try {
			FileInputStream r_path1 = new FileInputStream(objPath);
			BufferedReader b_read1 = new BufferedReader(new InputStreamReader(r_path1));
			model = new GLModel(b_read1, true, mtlPath, gl);
			r_path1.close();
			b_read1.close();

		} catch (Exception e) {
			System.out.println("LOADING ERROR" + e);
		}

		System.out.println("ModelLoaderOBJ init() done"); // ddd
		return model;
	}
}
