package oblig7;
import java.io.*;

public class Filehandler {
    private final int BUFFER = 1024 * 4; //how long we wont to look back
    int buffersize;
    StringBuffer stringBuffer;
    byte[] data;
    DataInputStream inputStream;
    DataOutputStream outputStream;
    public Filehandler(int buffer) {
        this.buffersize = buffer;
        stringBuffer = new StringBuffer(buffersize); //sets the capacity to the number of kb we want
        data = new byte[buffersize];
    }

    /**
     * Constructor, if no given buffer is passed in
     * we set the buffer size to our standard (1KB)
     */
    public Filehandler(){
        this.buffersize = BUFFER;
        stringBuffer = new StringBuffer(buffersize);
    }

    /**
     * Method to update the stringbuffer to never be bigger than our buffersize
     * if its too big, it is updated by removing the start of the buffer
     */
    private void updateStringBuffer(){
        //if the length of out stringbuffer is greater than our buffersize
        if (stringBuffer.length() > buffersize) {
            //we delete starting at the front
            stringBuffer.delete(0, stringBuffer.length() - buffersize);
        }
    }

    private DataInputStream infile(String path) throws IOException {
        try {
            return new DataInputStream(new BufferedInputStream(new FileInputStream(path)));
        } catch (IOException e){
            e.printStackTrace();;
        }
        return null;
    }

    public void  LZ77(String path) throws IOException{
        this.inputStream = infile(path);
        //data = new byte[inputStream.available()];
        this.outputStream = new DataOutputStream((new BufferedOutputStream(new FileOutputStream(path+"output"))));
        //DataInputStream streamCopy = inputStream;
        //streamCopy.readFully(data);
        String match = "";
        int next, tempIndex = 0, matchIndex = 0;
        //while there are more characters in the input stream
        while ((next = inputStream.read()) != -1){ //.read() reads -1 if empty
            tempIndex = stringBuffer.indexOf(match + (char)next); //if stringBuffer.indexOf() is empty, it will return -1
            //if it is a match
            if (tempIndex != -1){
                match += next;
                matchIndex = tempIndex;
            } else {
                String codedString = "~" + matchIndex + "~" + match.length() + "~" + (char)next;
                String currentString = match + (char)next;
                //if our coded string is shorter than the current string
                if (codedString.length() < currentString.length()) {
                    //we write the coded string into our output stream
                    outputStream.writeChars(codedString);
                    //and append the current string to the string buffer
                    stringBuffer.append(codedString);
                    //we reset the match and matchIndex to standard
                    match = "";
                    matchIndex = 0;
                } else {
                    //if the coded string is longer than the current string
                    //we set the match to our current string, and matchIndex to -1 (not found)
                    match = currentString;
                    matchIndex = -1;
                    //as long as there are characters left in the string, and we havent found another match
                    while (match.length() > 1 && matchIndex == -1){
                        //we  write the characters to the output stream
                        outputStream.write(match.charAt(0));
                        //we append the characters to the string buffer
                        stringBuffer.append(match.charAt(0));
                        //we update our match to the substring withouth the character we just removed
                        match = match.substring(1, match.length());
                        //we update the match index to the new index of the new match
                        matchIndex = stringBuffer.indexOf(match);
                    }
                }
                //update the string buffer to in case of length larger than our buffersize
                updateStringBuffer();
            }
        }
        if (matchIndex != 1){
            String codeString = "~"+matchIndex+"~"+match.length();
            if (codeString.length() <= match.length()){
                outputStream.writeChars(codeString);
            } else {
                outputStream.writeChars(match);
            }
        }
        inputStream.close();
        outputStream.flush();
        outputStream.close();
    }

    public void LZ77read(String path) throws IOException{
        inputStream = infile(path);
        assert inputStream != null;
        StreamTokenizer streamTokenizer = new StreamTokenizer(inputStream);
        streamTokenizer.ordinaryChar((int)' ');
        streamTokenizer.ordinaryChar((int)'.');
        streamTokenizer.ordinaryChar((int)'-');
        streamTokenizer.ordinaryChar((int)'\n');
        streamTokenizer.wordChars((int)'\n', (int)'\n');
        streamTokenizer.wordChars((int)' ', (int)'}');

        int offset, length; //offset and length for searching the compressed stream
        while (streamTokenizer.nextToken() != StreamTokenizer.TT_EOF){ //indicates if we have reached the end of the stream
            switch (streamTokenizer.ttype){
                case StreamTokenizer.TT_WORD:
                    stringBuffer.append(streamTokenizer.sval);
                    System.out.println(streamTokenizer.sval);
                    updateStringBuffer();
                    break;
                case StreamTokenizer.TT_NUMBER:
                    offset = (int) streamTokenizer.nval;
                    streamTokenizer.nextToken();
                    if (streamTokenizer.ttype == StreamTokenizer.TT_WORD){
                        //we habe a word
                        stringBuffer.append(offset+streamTokenizer.sval);
                        System.out.println(offset+streamTokenizer.sval);
                        break;
                    }
                    //we have a pointer
                    streamTokenizer.nextToken();
                    length = (int)streamTokenizer.nval;
                    //substring is from the offset to offset + length
                    String substring = stringBuffer.substring(offset, offset+length);
                    System.out.println(substring);
                    stringBuffer.append(substring);
                    updateStringBuffer();
                    break;
                default:
            }
        }
        inputStream.close();
    }

    public static void main(String[] args) throws IOException {
        String inpath = "src\\oblig7\\enkel";
        Filehandler filehandler = new Filehandler();
        filehandler.LZ77(inpath);
        //filehandler.LZ77read(inpath+".lz77");
    }
}
