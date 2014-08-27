package com.dangle.audplayer;

import java.util.Comparator;

public class LyricComparator implements Comparator<Lyric>
{
    public int compare(Lyric left, Lyric right) {
    	if (left.getPosition() < right.getPosition())
        	return -1;
        if (left.getPosition() > right.getPosition())
    		return 1;
        return 0;
    }
}
