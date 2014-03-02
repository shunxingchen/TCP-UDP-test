
import java.io.*;
import java.net.*;


public class TcpUdpTest {
	
	DatagramSocket udpSock = null;
	static InetAddress host = null;
	static int port;
	
	public static void main(String args[]) 
	{
			
		if(args.length < 1)
		{
			echo("Parameters missing!");
			return;
		}
		
		port = Integer.parseInt(args[0]);
		if(port!=7 && port!=13 && port!=37)
		{
			echo("Wrong port!");
			return;
		}
		
		try
        {
        	Runtime.getRuntime().exec("cmd.exe /c start java TcpUdpServer" +" "+port);
        }
        catch(IOException e)
        {
        	System.err.println("IOException " + e);
        }
		
		try
		{
			host = InetAddress.getByName("localhost");
		}
		catch(UnknownHostException e)
		{
			System.err.println("UnknownHostException " + e);
		}
		
		
		TcpUdpTest myMain = new TcpUdpTest();
		switch(port)
		{
			case 7:
				myMain.startRFC862();
				break;
			case 13:
				myMain.startRFC867();
				break;
			case 37:
				myMain.startRFC868();
				break;
		
		}
				    
	}
	
	
	//for RFC 862 request
	public void startRFC862()
	{
		try
		{			
		    String s; 
		    BufferedReader cin = new BufferedReader(new InputStreamReader(System.in));
        
		    udpSock = new DatagramSocket();
             
		    while(true)
		    {
		    	//take input and send the packet
		    	echo("Enter message to send by UDP : ");
		    	s = (String)cin.readLine();
		    	byte[] b = s.getBytes();
                 
		    	DatagramPacket  dp = new DatagramPacket(b , b.length , host , port);
		    	udpSock.send(dp);
                 
		    	//now receive reply
		    	//buffer to receive incoming data
		    	byte[] buffer = new byte[65536];
		    	DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
		    	udpSock.receive(reply);
                 
		    	byte[] data = reply.getData();
		    	s = new String(data, 0, reply.getLength());
                 
		    	//echo the details of incoming data - client ip : client port - client message
		    	echo(reply.getAddress().getHostAddress() + " : " + reply.getPort() + " - " + s);
		    	udpSock.close();
		    	echo("UDP client closed!");
		    	break;
		    }
		 }
         catch(IOException e)
		 {
        	 System.err.println("IOException " + e);
		 }
		  
		
		//start TCP request
		new Thread( new Runnable() {
		    @Override
		    public void run() 
		    {	
		    	String sentence;
		    	String modifiedSentence;

		    	BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
	
		    	try
		    	{

		    		Socket clientSocket = new Socket(host, port);

		    		DataOutputStream outToServer = new DataOutputStream(
		    		clientSocket.getOutputStream());

		    		BufferedReader inFromServer = new BufferedReader(new InputStreamReader(
		    		clientSocket.getInputStream()));

		    		echo("Enter message to send by TCP : ");
		    		sentence = inFromUser.readLine();

		    		outToServer.writeBytes(sentence + '\n');

		    		modifiedSentence = inFromServer.readLine();

		    		echo("FROM SERVER: " + modifiedSentence);

		    		clientSocket.close();
				echo("TCP client closed!");
		
		    	}
		    	catch(IOException e)
		    	{
		    		System.err.println("IOException " + e);
		    	}
	
		    }
		}).start();
		
	}



	//for RFC 867 request
	public void startRFC867()
	{
		try 
		{    
            DatagramPacket sendPak, receivePak;
            byte[] dummyData = new byte[1];
    		byte[] timeData  = new byte[256];
		 
    		udpSock = new DatagramSocket();
   	        		
    		while(true)
    		{
            	// create a datagram packet to send and recieve the reply.  the
            	// contents on send don't really matter for this application
            	sendPak = new DatagramPacket( dummyData, dummyData.length, host, port );
            	receivePak = new DatagramPacket( timeData, timeData.length );
            	//echo("-- Send DayTime request by UDP --");

            	// Send and receive, and time out after 5 seconds.
            	echo("Press Enter to send request for DayTime by UDP/TCP ...");  
            	try{System.in.read();}  
            	catch(Exception e){}

            	udpSock.send(sendPak);
            	udpSock.setSoTimeout(5000);
            	udpSock.receive(receivePak);

            	String received = new String( receivePak.getData() );
            	received = received.trim();

            	echo( "UDP DayTime: " + received );
            
            	// Close our socket
            	udpSock.close();
            	echo("UDP client closed!");
            	break;
    		}
        }
        catch (InterruptedIOException e) 
        {
        	System.err.println("Daytime: Timeout");
        }
        catch (Exception e) 
        {
        	System.err.println(e);
        }


		//start TCP request
		new Thread( new Runnable() {
			@Override
			public void run() 
			{
		    	try
				{
		    		Socket clientSocket = new Socket(host, port);
		    		
		    		BufferedReader inFromServer = new BufferedReader(new InputStreamReader(
				    		clientSocket.getInputStream()));
				
				    String time = inFromServer.readLine();	
				    echo("TCP DayTime: " + time);

				    clientSocket.close();
				    echo("TCP client closed!");
				 }
				 catch(IOException e)
				 {
				    System.err.println("IOException " + e);
				 }
			
			}
		}).start();

	}
	
	

	//for RFC 868 request
	public void startRFC868()
	{
	    try 
	    {   
	    	
	    	byte[] dummyData = new byte[1];
	    	byte[] timeData = new byte[256];

            DatagramPacket sendPacket, receivePacket;

            udpSock = new DatagramSocket();
            sendPacket = new DatagramPacket(dummyData, dummyData.length, host, port);
            receivePacket = new DatagramPacket(timeData, timeData.length);

		
            while(true)
            {	    
            	echo("Press Enter to send request for Time by UDP/TCP ...");  
            	try{System.in.read();}  
            	catch(Exception e){}
	
            	udpSock.send(sendPacket);
            	udpSock.setSoTimeout(5000);
                udpSock.receive(receivePacket);
                
                String received = new String( receivePacket.getData() );
            	received = received.trim();

            	echo("UDP Time: "+received);
            	udpSock.close();
            	echo("UDP client closed!");	
            	break; 
            }
        }
        catch (InterruptedIOException e) 
        {
        	System.err.println("Daytime: Timeout");
        }
        catch (Exception e) 
        {
        	System.err.println(e);
        }


	    //start TCP request
		new Thread( new Runnable() {
			@Override
			public void run() 
			{
		    	try
				{
		    		Socket clientSocket = new Socket(host, port);
		    		
		    		BufferedReader inFromServer = new BufferedReader(new InputStreamReader(
				    		clientSocket.getInputStream()));
				
				    String time = inFromServer.readLine();	
				    echo("TCP Time: " + time);

				    clientSocket.close();
				    echo("TCP client closed!");
				 }
				 catch(IOException e)
				 {
				    System.err.println("IOException " + e);
				 }
			
			}
		}).start();		
	}
	
	
    //simple function to echo data to terminal
    public static void echo(String msg)
    {
        System.out.println(msg);
    }

}