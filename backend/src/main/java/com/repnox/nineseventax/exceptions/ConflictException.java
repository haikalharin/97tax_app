package com.repnox.nineseventax.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.CONFLICT)
public class ConflictException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5990745442303094588L;
}
