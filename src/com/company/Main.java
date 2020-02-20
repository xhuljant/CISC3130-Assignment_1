package com.company;

import java.io.*;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        String filePathIn;
        ArrayList<Artist> allArtists = new ArrayList<>();

        while(true){

            //user can type stop in to stop program or provide a path for pgram to look for file
            System.out.print("Enter the file location ('STOP' to quit) :");

            filePathIn=in.next();
            System.out.println();

            //if the keyword stop is typed in then program quits
            if(filePathIn.equalsIgnoreCase("stop"))
                break;

                //using try to check to see if correct file path was entered. if incorrect then catch block prints out 'file not found' and while loop starts over
                try{
                    String line;
                    BufferedReader br =new BufferedReader(new FileReader(filePathIn));

                    //while loop paramter checks to see if the next line in the file is null, if it is then while loop does not execute
                    while((line=br.readLine()) != null) {

                        //line read in previously is placed into string array to make it possible to grab just the song name and artist name
                        //split by commas and ignoring commas inside parenthesis. Some songs have multiple featured artists and the commas inside the parenthesis might confuse program sp they are ignored
                        String[] lineArr = line.split(",(?![^()]*\\))", -1 );

                        String song = "";
                        String name = "";

                        //this if statement was put here to ignore first line of csv file that does not need to be read into file. If the line doesnt have more than 4 elements than the line is not turned into a Artist object
                        if (lineArr.length > 4) {
                            //grabbing song title and name from line of text and ignoring the rest
                            for (int i = 0; i < lineArr.length; i++) {
                                if (i == 1)
                                    song = lineArr[1];
                                if (i == 2)
                                    name = lineArr[2];
                            }

                            //creating a temporary Artist object to compare with objects already in array
                            Artist temp = new Artist(name, song);

                            //if array contains temp artist obj then we loop through all array objects to find the matching name and add song to that artist objects song array
                            if (allArtists.contains(temp)) {
                                for (int i = 0; i < allArtists.size(); i++) {
                                    if (allArtists.get(i).equals(temp))
                                        allArtists.get(i).addSong(song);
                                }
                            } else //if array does not contain artist object then temp obj is added to array
                                allArtists.add(temp);

                        }
                    }

                    //sorts the allartists array by total amount of songs from most songs to least
                    Collections.sort(allArtists, new songCompare());

                    //creating a new file and writing every element of allartists line by line
                    PrintWriter pw = new PrintWriter(new File("SortedByNumberOfSongs.txt"));
                    for(int i = allArtists.size()-1; i>0; i--){
                        if(allArtists.get(i).getTotalSongsOnCharts()<2)
                            pw.write(allArtists.get(i).getName()+" has "+allArtists.get(i).getTotalSongsOnCharts()+" song on the charts.\n");
                        else
                            pw.write(allArtists.get(i).getName()+" has "+allArtists.get(i).getTotalSongsOnCharts()+" songs on the charts.\n");
                    }

                    //using custom sort class to resort names in array list in alphabetical order
                    Collections.sort(allArtists, new nameCompare());

                    PrintWriter printWriter = new PrintWriter(new File("ArtistsInAlphabeticalOrder.txt"));
                    for(int i = 1; i<allArtists.size(); i++){
                        printWriter.write(allArtists.get(i).getName()+"\n");
                    }


                    pw.close();
                    printWriter.close();

                    System.out.println("Two files have been created. One in alphabetical order and one from most songs on top charts to least.");

                    //in case filename is entered wrong or file doesnt exists program prompts for a correct file name
                    } catch (FileNotFoundException e) {
                    System.out.println("FILE NOT FOUND. Please enter correct file name.");
                    } catch (IOException e) {
                    e.printStackTrace();
                    }
            }

    }


}

class Artist implements Comparable<Artist>{

    private String name;

    //songs are stored in a arraylist to make it easier to add songs as they come by in the text file
    private LinkedList<String> songs=new LinkedList<>();

    //everytime a song is added to Artist Obj totalSongsOnCHarts is incremented by 1 to keep track of all songs on the charts
    private int totalSongsOnCharts;

    public Artist(String name, String song){

        //if statements are used to check the formatting of the names of artists to make sorting easier later and for better uniformity
        if(name.equalsIgnoreCase(""))
            this.name=name;
        else if(name.charAt(0)=='"')
            this.name=name.substring(1,name.length()-1);
        else
            this.name=name;

        this.songs.add(song);
        this.totalSongsOnCharts++;

    }

    //method to add songs to arraylist of Artist Obj that already exists
    public void addSong(String song){
        this.songs.add(song);
        this.totalSongsOnCharts++;

    }

    //prints out artist name as well as all songs, mainly for testing
    public String toString(){
        String out=name+" :";
        for(int i=0;i<songs.size();i++ ){
            out+=" "+songs.get(i)+",";
        }

        if(totalSongsOnCharts<2)
            out+="  (1 Song on the chart.)";
        else
            out+="  ("+totalSongsOnCharts+" Songs on the charts.)";

        return out;
    }

    public int getTotalSongsOnCharts(){
        return this.totalSongsOnCharts;
    }


    public String getName(){
        return this.name;
    }

    //overrode equals method so that when .contains is called on array lists, true is returned if the name exists even if the songs dont match. Without overriding equals, java checks for exact clone
    @Override
    public boolean equals(Object obj) {
        if(obj==null)
            return false;

        //checking to see if the object passed in is the same class type
        if(!Artist.class.isAssignableFrom(obj.getClass()))
            return false;

        //creating a temp artist object by casting Artist to obj. We can no call .getName method of Artist, unable to do when its a generic Object
        Artist other = (Artist)obj;

        //if the name of object passed in equals name of this object then true is returned
        if(this.name.equalsIgnoreCase(other.getName()))
            return true;

        return false;
    }

    //overrode comapreto method but never used
    @Override
    public int compareTo(Artist other) {
        if(this.getName().equalsIgnoreCase(other.getName()))
            return 1;

        return -1;
    }
}

//overriding default collections.sort method so it will check specifically for names only
class nameCompare implements Comparator<Artist> {

    @Override
    public int compare(Artist a, Artist b) {

        return a.getName().compareToIgnoreCase(b.getName());
    }
}

//overriding default collections.sort method so it will check specifically for total songs on charts only
class songCompare implements Comparator<Artist> {

    @Override
    public int compare(Artist a, Artist b) {

        if(a.getTotalSongsOnCharts()>(b.getTotalSongsOnCharts()))
            return 1;
        else if(a.getTotalSongsOnCharts()==(b.getTotalSongsOnCharts()))
            return 0;
        else
            return -1;
    }
}



