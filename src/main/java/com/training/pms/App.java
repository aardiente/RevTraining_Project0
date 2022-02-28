package com.training.pms;

import java.sql.Connection;
import java.sql.DriverManager;

import com.training.pms.Engine.Engine;

/**
 * Entry point
 *
 */
public class App 
{
    public static void main( String[] args )
    {
    	Engine.startEngine();
    }
}

