/*
 * File:                HashFunction.java
 * Authors:             Jonathan T. McClain
 * Company:             Sandia National Laboratories
 * Project:
 *
 * Copyright Oct 10, 2011, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 * Reviewers:
 * Review Date:
 * Review Comments:
 *
 * Revision History:
 *
 * $Log: HashFunction.java,v $
 */

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package replete.hash;

/**
 *
 * @author jtmccl
 */
public interface HashFunction<T> {
    long hash(T item);
}
