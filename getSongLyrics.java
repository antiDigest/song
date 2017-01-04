import java.io.IOException;
 
import java.util.ArrayList;
import java.util.List;
import java.util.*;
import java.util.Iterator;
 
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import java.nio.file.*;
import java.nio.charset.Charset;
 
public class getSongLyrics {
 
    private final static String songLyricsURL = "http://www.metrolyrics.com";
    private final static String songTitlesURL = "http://www.metrolyrics.com/top100.html";

    public static List<List<String> > getSongTitle() throws IOException{
        List<String> songtitles= new ArrayList<String>();
        List<String> songurls= new ArrayList<String>();
        List<String> entry = new ArrayList<String>();

        Path file = Paths.get("song_names.txt");
        
        Document doc = Jsoup.connect(songTitlesURL).get();
        Elements p = doc.select("a.song-link");
        for (Element e: p) {
            String line = null;
            for (Node k: e.childNodes()) {
                if (k instanceof TextNode) {
                    songtitles.add(((TextNode)k).getWholeText());
                    line = ((TextNode)k).getWholeText();
                }
            }
            songurls.add(e.attr("href"));
            entry.add(line+" ; "+e.attr("href"));
            
        }
        Elements p1 = doc.select("a.title");
        for (Element e: p1) {
            String line = null;
            for (Node k: e.childNodes()) {
                if (k instanceof TextNode) {
                    songtitles.add(((TextNode)k).getWholeText());
                    line = ((TextNode)k).getWholeText();
                }
            }
            songurls.add(e.attr("href"));
            entry.add(line+" ; "+e.attr("href"));
        }

        Files.write(file, entry);

        List<List<String> > songs = new ArrayList<List<String> >(2);
        songs.add(songtitles);
        songs.add(songurls);
        return songs;
    }


    public static List<String> getSong(String songUrl) throws IOException {
        List<String> lyrics= new ArrayList<String>();

        Document doc = Jsoup.connect(songUrl).get();
        Elements p = doc.select("p.verse");
        for (Element e: p) {
            for (Node k: e.childNodes()) {
                if (k instanceof TextNode) {
                    String line = ((TextNode)k).getWholeText();
                    lyrics.add(line.replace("\t", "-"));
                }
            }
        }
        return lyrics;
    }

    public static void main(String[] args) throws IOException {
        List<List<String> > songs = getSongLyrics.getSongTitle();
        List<String> songTitles = songs.get(0);
        List<String> songUrls = songs.get(1);
        ListIterator<String> song = songUrls.listIterator();
        String name = song.next();

        while(song.hasNext()){
            List<String> songLyrics = getSongLyrics.getSong(name);
            int index = song.previousIndex();
            Path file = Paths.get("lyrics/"+songTitles.get(index).replace(" ", "-")+".txt");
            Files.write(file, songLyrics, Charset.forName("UTF-8"));
            name = song.next();
        }
            
    }
}

