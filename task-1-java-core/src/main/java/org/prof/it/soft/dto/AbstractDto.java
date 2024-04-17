package org.prof.it.soft.dto;

import java.io.Serializable;

/**
 * This is an abstract class that represents a Data Transfer Object (DTO).
 * It implements the Serializable interface, which means it can be converted
 * into a byte stream and sent over the network or saved into a file.
 *
 * Any class that represents a DTO should extend this class.
 */
public abstract class AbstractDto implements Serializable {
}
