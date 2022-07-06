package com.engie.csai.pc.core.exception;

public class ParallelCommitteeException extends RuntimeException{

    public ParallelCommitteeException(String message){
        super(message);
    }
    public ParallelCommitteeException(Exception e){
        super(e);
    }
}
