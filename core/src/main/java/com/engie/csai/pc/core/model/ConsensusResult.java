package com.engie.csai.pc.core.model;

import java.util.Objects;

public class ConsensusResult implements Comparable<ConsensusResult>
{

    private String threadId;
    private String answer;
    private Long executionTime;


    public ConsensusResult(String threadId, String answer, Long executionTime)
    {
        super();
        this.threadId = threadId;
        this.answer = answer;
        this.executionTime = executionTime;
    }

    public String getThreadId()
    {
        return threadId;
    }

    public void setThreadId(String threadId)
    {
        this.threadId = threadId;
    }

    public String getAnswer()
    {
        return answer;
    }

    public void setAnswer(String answer)
    {
        this.answer = answer;
    }

    public long getExecutionTime()
    {
        return executionTime;
    }

    public void setExecutionTime(long executionTime)
    {
        this.executionTime = executionTime;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(threadId);
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ConsensusResult other = (ConsensusResult) obj;
        return Objects.equals(threadId, other.threadId);
    }

    @Override
    public int compareTo(ConsensusResult o)
    {
        if (o == null)
        {
            return 1;
        }
        if (this == o)
        {
            return 0;
        }
        return this.executionTime.compareTo(o.getExecutionTime());
    }

    @Override
    public String toString()
    {
        return "thread id: " + this.threadId + ", answer: " + this.answer + ", execution time: " + this.executionTime;
    }

}
