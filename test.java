package assignment2;

import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

public class test {
    public static void main(String[] args) throws IOException, ParserConfigurationException, SAXException {


        //test case 1-----------------------------------------------------------------
        String actualResult1 = new ContentServer1().read("src/assignment2/data1.txt");

        String expectedResult1 ="<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n" +
                "<feed xml:lang=\"en-US\" xmlns=\"http://www.w3.org/2005/Atom\">\n" +
                "          <title>Hello World</title>\n" +
                "          <subtitle>for ContentServer1 test</subtitle>\n" +
                "          <updated>2021-08-07T18:30:02Z</updated>\n" +
                "          <author>\n" +
                "                    <name>Huatao Dong</name>\n" +
                "          </author>\n" +
                "          <id>urn::uuid:60a76c80-d399-11d9-b93C-0003939e0af6</id>\n" +
                "          <entry>\n" +
                "                    <title>for entry test</title>\n" +
                "                    <link>www.cs.adelaide.edu.au/users/third/ds/</link>\n" +
                "                    <id>urn:uuid:1225c695-cfb8-4ebb-aaaa-80da344efa6a</id>\n" +
                "                    <updated>2021-08-07T18:30:02Z</updated>\n" +
                "                    <summary>here is some plain text. i love ds. i love ds.</summary>\n" +
                "          </entry>\n" +
                "          <title>second feed entry</title>\n" +
                "          <link>www.cs.adelaide.edu.au/users/third/ds/14ds2s1</link>\n" +
                "          <id>urn:uuid:1225c695-cfb8-4ebb-aaaa-80da344efa6b</id>\n" +
                "          <updated>2015-08-07T18:29:02Z</updated>\n" +
                "          <summary>here's another summary entry which a reader would normally use to work out if they wanted to read some more. It's quite handy.</summary>\n" +
                "</feed>\n";

        if(actualResult1.equals(expectedResult1)){
            System.out.println("test case 1 passed");
        }else{
            System.out.println("test case 1 failed");
        }


        //test case 2-----------------------------------------------------------------
        String actualResult2 = new ContentServer1().read("src/assignment2/data2.txt");

        String expectedResult2 ="<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n" +
                "<feed xml:lang=\"en-US\" xmlns=\"http://www.w3.org/2005/Atom\">\n" +
                "          <title>Hello World</title>\n" +
                "          <subtitle>for ContentServer2 test</subtitle>\n" +
                "          <link>www.google.com</link>\n" +
                "          <updated>2021-09-07Ta:30:02Z</updated>\n" +
                "          <author>\n" +
                "                    <name>Huatao Dong</name>\n" +
                "          </author>\n" +
                "          <id>urn::uuid:60a76c80-d399-11d9-b93C-0003939e0af6</id>\n" +
                "          <entry>\n" +
                "                    <title>test for entry</title>\n" +
                "                    <link>www.cs.adelaide.edu.au/users/third/ds/</link>\n" +
                "                    <id>urn:uuid:1225c695-cfb8-4ebb-aaaa-80da344efa6a</id>\n" +
                "                    <updated>2021-08-07T18:30:02Z</updated>\n" +
                "                    <summary>here is the summary.</summary>\n" +
                "          </entry>\n" +
                "</feed>";

        if(actualResult2==expectedResult2){
            System.out.println("test case 2 passed");
        }else{
            System.out.println("test case 2 failed");
        }

        //test case 3-----------------------------------------------------------------
        String xml1 ="<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n" +
                "<feed xml:lang=\"en-US\" xmlns=\"http://www.w3.org/2005/Atom\">\n" +
                "          <title>Hello World</title>\n" +
                "          <subtitle>for ContentServer1 test</subtitle>\n" +
                "          <updated>2021-08-07T18:30:02Z</updated>\n" +
                "          <author>\n" +
                "                    <name>Huatao Dong</name>\n" +
                "          </author>\n" +
                "          <id>urn::uuid:60a76c80-d399-11d9-b93C-0003939e0af6</id>\n" +
                "          <entry>\n" +
                "                    <title>for entry test</title>\n" +
                "                    <link>www.cs.adelaide.edu.au/users/third/ds/</link>\n" +
                "                    <id>urn:uuid:1225c695-cfb8-4ebb-aaaa-80da344efa6a</id>\n" +
                "                    <updated>2021-08-07T18:30:02Z</updated>\n" +
                "                    <summary>here is some plain text. i love ds. i love ds.</summary>\n" +
                "          </entry>\n" +
                "          <title>second feed entry</title>\n" +
                "          <link>www.cs.adelaide.edu.au/users/third/ds/14ds2s1</link>\n" +
                "          <id>urn:uuid:1225c695-cfb8-4ebb-aaaa-80da344efa6b</id>\n" +
                "          <updated>2015-08-07T18:29:02Z</updated>\n" +
                "          <summary>here's another summary entry which a reader would normally use to work out if they wanted to read some more. It's quite handy.</summary>\n" +
                "</feed>\n" +
                "\n";

        String actualResult3 = new GETClient1().parseXML(xml1);
        String expectedResult3 ="------------------------------------\n" +
                "title:Hello World\n" +
                "subtitle:for ContentServer1 test\n" +
                "author:Huatao Dong\n" +
                "id:urn::uuid:60a76c80-d399-11d9-b93C-0003939e0af6\n" +
                "entry\n" +
                "link : www.cs.adelaide.edu.au/users/third/ds/\n" +
                "title : Hello World\n" +
                "id : urn::uuid:60a76c80-d399-11d9-b93C-0003939e0af6\n" +
                "updated : 2021-08-07T18:30:02Z\n" +
                "summary : here is some plain text. i love ds. i love ds.\n" +
                "entry\n" +
                "title:second feed entry\n" +
                "link:www.cs.adelaide.edu.au/users/third/ds/14ds2s1\n" +
                "id:urn:uuid:1225c695-cfb8-4ebb-aaaa-80da344efa6b\n" +
                "summary:here's another summary entry which a reader would normally use to work out if they wanted to read some more. It's quite handy.\n";

        if(actualResult3.equals(expectedResult3)){
            System.out.println("test case 3 passed");
        }else{
            System.out.println("test case 3 failed");
        }


        //test case 3-----------------------------------------------------------------

        String xml2 ="<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n" +
                "<feed xml:lang=\"en-US\" xmlns=\"http://www.w3.org/2005/Atom\">\n" +
                "          <title>Hello World</title>\n" +
                "          <subtitle>for ContentServer2 test</subtitle>\n" +
                "          <link>www.google.com</link>\n" +
                "          <updated>2021-09-07Ta:30:02Z</updated>\n" +
                "          <author>\n" +
                "                    <name>Huatao Dong</name>\n" +
                "          </author>\n" +
                "          <id>urn::uuid:60a76c80-d399-11d9-b93C-0003939e0af6</id>\n" +
                "          <entry>\n" +
                "                    <title>test for entry</title>\n" +
                "                    <link>www.cs.adelaide.edu.au/users/third/ds/</link>\n" +
                "                    <id>urn:uuid:1225c695-cfb8-4ebb-aaaa-80da344efa6a</id>\n" +
                "                    <updated>2021-08-07T18:30:02Z</updated>\n" +
                "                    <summary>here is the summary.</summary>\n" +
                "          </entry>\n" +
                "</feed>\n";

        String actualResult4 = new GETClient1().parseXML(xml2);
        String expectedResult4 = "------------------------------------\n" +
                "title:Hello World\n" +
                "subtitle:for ContentServer2 test\n" +
                "link:www.google.com\n" +
                "author:Huatao Dong\n" +
                "id:urn::uuid:60a76c80-d399-11d9-b93C-0003939e0af6\n" +
                "entry\n" +
                "link : www.google.com\n" +
                "title : Hello World\n" +
                "id : urn::uuid:60a76c80-d399-11d9-b93C-0003939e0af6\n" +
                "updated : 2021-09-07Ta:30:02Z\n" +
                "summary : here is the summary.\n" +
                "entry\n";

        if(actualResult4.equals(expectedResult4)){
            System.out.println("test case 4 passed");
        }else{
            System.out.println("test case 4 failed");
        }
    }

}
