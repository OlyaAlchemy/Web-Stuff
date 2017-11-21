import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.HashSet;

public class WikiPhilosophy {
    Elements elements;
    String element;
    private final String adress = "https://en.wikipedia.org/wiki/Special:Random";
    //    private final String adress = "https://en.wikipedia.org/wiki/Toni_Seifert";
    private final String finalAdress = "https://en.wikipedia.org/wiki/Philosophy";
    private String nextLink;
    private final int count = 100;
    private HashSet<String>[] links = new HashSet[2];   //1 - links that go to philosophy, 0 - that don't
    HashSet<String> tempLinks = new HashSet<>();
    private int percents = 0;

    public WikiPhilosophy() {
        links[0] = new HashSet<String>();
        links[1] = new HashSet<String>();
    }

    public void findIt() throws IOException {
        for (int i = 0; i < count; i++) {
            Document page = Jsoup.connect(adress).get();
            System.out.println(i + ": " + page.location());
            if (page.location().equals(finalAdress)) {
                i--;
                break;
            } else if ((links[0].contains(page.location()) == false) && (links[1].contains(page.location()) == false)) {
                putLinksInHashes(page.location());
            } else {
                i--;
                break;
            }
        }
        int fullSize = links[0].size() + links[1].size();
        double size0 = links[0].size() * 100 / fullSize;
        double size1 = links[1].size() * 100 / fullSize;
        System.out.println("Number of original links: " + fullSize);
        System.out.println("Number of links that do to philosophy: " + links[1].size() + " - " + size1 + "%");
        System.out.println("Number of links that dos't to philosophy: " + links[0].size() + " - " + size0 + "%");
        System.out.println("Links that do to philosophy:");
        for (String st : links[1]) {
            System.out.println(st);
        }
        System.out.println("___________________________________________");
        System.out.println("Links that dos't to philosophy:");
        for (String st : links[0]) {
            System.out.println(st);
        }
    }

    public void putLinksInHashes(String link) throws IOException {
        if (link.equals(finalAdress)) {
            for (String st : tempLinks) {
                links[1].add(st);
            }
            tempLinks.clear();
        } else if (tempLinks.contains(link) || link.equals("https://en.wikipedia.org/wikinull")) {
            for (String st : tempLinks) {
                if (st != "https://en.wikipedia.org/wikinull") {
                    links[0].add(st);
                }
            }
            tempLinks.clear();
        } else {
            putLinksInTempHash(link);
        }
    }

    public void putLinksInTempHash (String link) throws IOException {
        tempLinks.add(link);
        Document page = Jsoup.connect(link).get();
        String article = page.body().getElementsByTag("p").html();
        try {
            String foundLink = "https://en.wikipedia.org/wiki" + findValidLink(article);
            if(links[0].contains(foundLink)){
                for (String st : tempLinks) {
                    links[0].add(st);
                }
                tempLinks.clear();
            } else if(links[1].contains(foundLink)){
                for (String st : tempLinks) {
                    links[1].add(st);
                }
                tempLinks.clear();
            } else {
                System.out.println("              " + foundLink);
                putLinksInHashes(foundLink);
            }
        } catch (Exception ex) {
            putLinksInHashes(link);
            System.out.println("Exception here:" + link);
        }
    }

    public String findValidLink(String args) throws Exception {
        int indOfFoundWikiLink = args.indexOf("/wiki/");
        int openedBracketAfterLink = args.indexOf("(");
        int closedBracketAfterLink = args.indexOf(")", openedBracketAfterLink);
        if (indOfFoundWikiLink == -1) {
            return null;
        } else if (openedBracketAfterLink > indOfFoundWikiLink) {
            return args.substring(indOfFoundWikiLink + 5, args.indexOf(" ", indOfFoundWikiLink) - 1);
        } else if (closedBracketAfterLink < indOfFoundWikiLink) {
            return args.substring(indOfFoundWikiLink + 5, args.indexOf(" ", indOfFoundWikiLink) - 1);
        } else {
            args = findValidLink(args.substring(closedBracketAfterLink));
            return args;
        }
    }

    public static void main(String[] args) throws IOException {
        WikiPhilosophy wikiPhilosophy = new WikiPhilosophy();
        wikiPhilosophy.findIt();
    }
}
