
import java.io.*;
import java.net.*;
import java.util.Date;

public class TcpUdpServer {
	
	private static DatagramSocket udpSock = null;
	private static ServerSocket tcpSock = null;
	private static int port;
	
	public static void main(String args[])
    {        
		port = Integer.parseInt(args[0]);
		
		//creating a server socket, parameter is local port number
		try
		{
			udpSock = new DatagramSocket(port);
			System.out.println("UDP Server waiting for client on port " + udpSock.getLocalPort());
			tcpSock = new ServerSocket(port);
			System.out.println("TCP Server waiting for client on port " + tcpSock.getLocalPort());
		}
		catch(IOException e)
		{
			System.err.println("IOException " + e);
		}
		
		TcpUdpServer myServer = new TcpUdpServer();

        switch(port)
        {
			case 7:
				myServer.startRFC862();
				break;
			case 13:
				myServer.startRFC867();
				break;
			case 37:
				myServer.startRFC868();
				break;
			default:
				echo("wrong port!");
				return;

        } 
        
    }


	//for RFC 862 response
    public void startRFC862()
    {
    	

    	String clientSentence;
        String capitalizedSentence;

        while (true) 
        {
        	
            try
            {       	 
          
            	/*******************************************************************
            	 * * Handle UDP * *
            	 *******************************************************************/ 
        	 
            	//buffer to receive incoming data
            	byte[] buffer = new byte[65536];
            	DatagramPacket incoming = new DatagramPacket(buffer, buffer.length);
             
            	//Wait for an incoming data
            	echo("Server socket created. Waiting for incoming data...");
             
            	udpSock.receive(incoming);
                
            	byte[] data = incoming.getData();
            	String s = new String(data, 0, incoming.getLength());
                 
            	//echo the details of incoming data - client ip : client port - client message
            	echo(incoming.getAddress().getHostAddress() + " : " + incoming.getPort() + " - " + s);
                 
            	s = "UDP OK : " + s;
            	DatagramPacket dp = new DatagramPacket(s.getBytes() , s.getBytes().length , incoming.getAddress() , incoming.getPort());
            
            	udpSock.send(dp);
            	udpSock.close();
            	echo("UDP server closed!");



            	/*******************************************************************
            	 * * Handle TCP * *
            	 *******************************************************************/	

            	Socket connectionSocket = tcpSock.accept();

            	BufferedReader inFromClient = new BufferedReader(
                    new InputStreamReader(connectionSocket.getInputStream()));

            	DataOutputStream outToClient = new DataOutputStream(
                    connectionSocket.getOutputStream());

            	clientSentence = inFromClient.readLine();
            	//echo with capital for difference from UDP echo
            	capitalizedSentence = clientSentence.toUpperCase() + '\n';

            	outToClient.writeBytes(capitalizedSentence);

            	connectionSocket.close(); 
            	echo("TCP server closed!");
            	break;	
            }         
            catch(IOException e)
            {
            	e.printStackTrace();
            }
	
        }

    }



    //for RFC 867 response
    public void startRFC867()
    {
    	
    	while(true)
    	{
    		try
            {       	 
             
    	       /*******************************************************************
                * * Handle UDP * *
                *******************************************************************/ 
           	 
               //buffer to receive incoming data
               byte[] buffer = new byte[1];
	       	
               DatagramPacket incoming = new DatagramPacket(buffer, buffer.length);
            	
               udpSock.receive(incoming);       
                    
               //echo the details of incoming data - client ip : client port - client message
               echo("UDP received from: "+incoming.getAddress().getHostAddress() + " : " + incoming.getPort());
                 
               byte[] outBuffer = new java.util.Date ().toString ().getBytes ("latin1");
               incoming.setData (outBuffer);
               incoming.setLength (outBuffer.length);
               udpSock.send (incoming);
               udpSock.close();
               echo("UDP server closed!");


               /*******************************************************************
                * * Handle TCP * *
                *******************************************************************/
               Socket connectionSocket = tcpSock.accept();

               DataOutputStream outToClient = new DataOutputStream(
                       connectionSocket.getOutputStream());

               outBuffer = new java.util.Date ().toString ().getBytes ("latin1");
               outToClient.write(outBuffer);

               connectionSocket.close(); 
               echo("TCP server closed!");
               break;	
            }         
            catch(IOException e)
            {
                e.printStackTrace();
            }
    	}
    }
    
    
    
    //for RFC 868 response
    public void startRFC868()
    {
    	while(true)
    	{
    		try
            {       	 
             
    	       /*******************************************************************
                * * Handle UDP * *
                *******************************************************************/ 
           	 
               //buffer to receive incoming data
               byte[] buffer = new byte[1];
	       	
               DatagramPacket incoming = new DatagramPacket(buffer, buffer.length);	       
	
               udpSock.receive(incoming);       
	
               //echo the details of incoming data - client ip : client port - client message
               echo("UDP received from: "+incoming.getAddress().getHostAddress() + " : " + incoming.getPort());      
		
               long secondsSince1900 = getTime();
               String time = Long.toString(secondsSince1900);
               byte[] byteTime = time.getBytes();	 	
               incoming.setData (byteTime);
               incoming.setLength (byteTime.length);
               udpSock.send (incoming);
               udpSock.close();
               echo("UDP server closed!");	


               /*******************************************************************
                * * Handle TCP * *
                *******************************************************************/
               Socket connectionSocket = tcpSock.accept();

               DataOutputStream outToClient = new DataOutputStream(
                       connectionSocket.getOutputStream());

               secondsSince1900 = getTime();
               time = Long.toString(secondsSince1900);
               byteTime = time.getBytes();
               outToClient.write(byteTime);

               connectionSocket.close(); 
               echo("TCP server closed!");
               break;	
            }         
            catch(IOException e)
            {
                e.printStackTrace();
            }
    	}
    	
    }    	


    //simple function to echo data to terminal
    public static void echo(String msg)
    {
        System.out.println(msg);
    }


    public static long getTime() {
       long differenceBetweenEpochs = 2208988800L;
       Date now = new Date();
       long secondsSince1970 = now.getTime() / 1000;
       long secondsSince1900 = secondsSince1970 + differenceBetweenEpochs;
       return secondsSince1900;
   }

}