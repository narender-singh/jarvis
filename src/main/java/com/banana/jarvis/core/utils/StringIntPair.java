package com.banana.jarvis.core.utils;

import java.util.Comparator;
import java.util.Objects;

public final class StringIntPair {

	  public static final Comparator<StringIntPair> NR_COMPARATOR = new Comparator<StringIntPair>() {

	    @Override
	    public int compare(final StringIntPair o1, final StringIntPair o2) {
	      return o1.number - o2.number;
	    }
	  };
	  
	  private final int number;

	  private final String string;

	  public StringIntPair(final int number, final String string) {
	    this.number = number;
	    this.string = string;
	  }

	  public int getNumber() {
	    return number;
	  }

	  public String getString() {
	    return string;
	  }

	  @Override
	  public String toString() {
	    return "StringNumberPair{" + "number=" + number + ", string=" + string + '}';
	  }

	  @Override
	  public int hashCode() {
	    int hash = 3;
	    hash = 97 * hash + this.number;
	    return 97 * hash + Objects.hashCode(this.string);
	  }

	  @Override
	  public boolean equals(final Object obj) {
	    if (obj == null) {
	      return false;
	    }
	    if (getClass() != obj.getClass()) {
	      return false;
	    }
	    final StringIntPair other = (StringIntPair) obj;
	    if (this.number != other.number) {
	      return false;
	    }
	    return Objects.equals(this.string, other.string);
	  }

	}