package oblig7;

import java.io.IOException;

public class DocumentDecompressor {

      public static void main(String[] args) throws IOException {
            String compressed = "src\\oblig7\\compressed";
            String decompressed = "src\\oblig7\\decompressed";

            DocumentDecompressor d = new DocumentDecompressor();
            d.decompressDocument(compressed, decompressed);
      }

      public void decompressDocument(String inPath, String outPath) throws IOException {
            Huffman huffman = new Huffman();
            byte[] decompressedBytes = huffman.decompress(inPath);
            LZ77 lz77 = new LZ77();
            lz77.deCompress(decompressedBytes, outPath);

      }
}
