package com.trivolous.game.domain;

import java.util.Date;

// TODO -- rename to TrivolousFormat or MyFormat or something like that.
public class DurationFormat {
	public long mins;
	public long hours;
	public long days;
	public DurationFormat(long ms) 
	{
		long secs = ms/1000;
		mins = secs/60;
		hours = mins/60;
		days = hours/24;
		mins = mins % 60;
		hours = hours % 24;
	}
	
   public String daysHoursMinsLong()
   {
		
			String text = new String();
			if (days != 0)
			{
				text += "" + days + " day";
				if (days > 1) text += "s";
				text += " ";
			}
			
			if (hours != 0)
			{
				text += "" + hours + " hour";
				if (hours > 1) text += "s";
			}
			
			text += " " + mins + " min";
			if (mins > 1) text += "s";
			
			
			return text;

	   }

	   public String timeAgoString()
	   {

			if (days > 1)
			{
				return "" + days + " days ago";
			}
			if (days == 1)
			{
				return "1 day ago";
			}
			if (hours > 1)
			{
				return "" + hours + " hours ago";
			}
			if (hours == 1)
			{
				return "1 hour ago";
			}
			if (mins > 1)
			{
				return "" + mins + " minutes ago";
			}
			if (mins == 1)
			{
				return "1 minute ago";
			}
			return "just now";
	   }

   		public String daysHoursMins()
	   {
			String text = new String();
			if (days != 0)
			{
				text += "" + days + "d ";
			}
			
			if (hours != 0)
			{
				text += "" + hours + "h ";
			}
			
			text += " " + mins + "m ";
			
			return text;

	   }
	   
 	   public static String daysHoursMinsLongStatic(long ms)
 	   {
 		   DurationFormat df = new DurationFormat(ms);
 		   return df.daysHoursMinsLong();
 	   }

	   public static String timeAgoStatic(Date then)
	   {
		   Date now = new Date();
		   long ms = now.getTime() - then.getTime();
		   DurationFormat df = new DurationFormat(ms);
		   return df.timeAgoString();
	   }

	   public static String upToNCharacters(String s, int n) {
			if (s.length()<=n)
			{
				return	s;
			}
			else
			{
				return	s.substring(0, n-3) + "...";
			}
		}
		
	   public static String text2html(String s) {
		    StringBuilder builder = new StringBuilder();
		    int index = 0;
		    boolean inlink = false;
		    int linkstartpos = 0;
		    while( index < s.length()) {
		    	char c = s.charAt(index);
		    	
		    	if (inlink) {
			        if (" \n".indexOf(c) != -1 || index == (s.length() - 1) ) {
			        	 // if one from end move to end	
			        	 if (index == s.length() - 1) index += 1;
			    		 String link = s.substring(linkstartpos, index);
			    		 builder.append("<a href=\"http://" + link + "\">" +link + "</a>");
			    		 inlink=false;
			    		 // move back index to process end of link char (if at end, will exit because it was incremented earlier)
			    		 index -= 1;
			        }
		    	} else
		    	{
			    	if (s.regionMatches(index, "http://", 0, 7)) {
			    		inlink = true;
			    		index += 7;
			    		linkstartpos = index;
			    	} else {	
				    	
				        switch(c) {
				            case '<': builder.append("&lt;"); break;
				            case '>': builder.append("&gt;"); break;
				            case '&': builder.append("&amp;"); break;
				            case '"': builder.append("&quot;"); break;
				            case '\'': builder.append("&apos;"); break;
				            case '\n': builder.append("<br>"); break;
				            default:
				                if( c < 128 ) {
				                    builder.append(c);
				                } else {
				                    builder.append("&#").append((int)c).append(";");
				                }    
				        }
			    	}
		    	}
		    	++index;
		    }
		    return builder.toString();
		}

	   public static String fmtPts(int pts)
	   {
		   if (pts <= 0) return ""+pts;
		   else return "+"+pts;
	   }

	   
}
