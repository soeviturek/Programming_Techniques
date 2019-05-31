package main;

import java.io.IOException;

import app.Menu;
import exceptions.InvalidDate;
import exceptions.InvalidId;

public class Driver {

	public static void main(String[] args) 
	{
		Menu menu = new Menu();
		try {
		menu.run();
		}catch (InvalidId ii) {
		} catch (InvalidDate id){
		}catch (IOException io){
		}

	}

}
