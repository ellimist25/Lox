package com.craftinginterpreters.lox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.craftinginterpreters.lox.TokenType.*;

/**
 * The core of the Scaner is a loop. Starting at the first character of the source code,
 * the scanner figures out what lexeme the character belongs to, and consumes it and any following characters
 * that are part of that lexeme. When it reaches the end of that lexeme, it emits a token.
 * Then it loops back and does it again, starting from the very next character in the source code.
 * It keeps doing that, eating characters and outputting tokens, until it reaches the end of the input.
 */

public class Scanner {
	private final String source; // A single line of code
	private final List<Token> tokens = new ArrayList<>();
	private int start = 0;
	private int current = 0;
	private int line = 1;

	public Scanner(String source) {
		this.source = source;
	}

	List<Token> scanTokens() {
		while (!isAtEnd()) {
			// We are at the beginning of the next lexeme.
			this.start = this.current;
			scanToken();
		}
		this.tokens.add(new Token(EOF, "", null, line));
		return this.tokens;
	}

	/**
	 * This checks to see if we are the end of the current token or not
	 * by comparing the length of the token to where it currently is reading
	 */
	private boolean isAtEnd() {
		return this.current >= this.source.length();
	}

	private void scanToken() {
		char c = advance();
		switch (c) {
			case '(': addToken(LEFT_PAREN); break;
			case ')': addToken(RIGHT_PAREN); break;
			case '{': addToken(LEFT_BRACE); break;
			case '}': addToken(RIGHT_BRACE); break;
			case ',': addToken(COMMA); break;
			case '.': addToken(DOT); break;
			case '-': addToken(MINUS); break;
			case '+': addToken(PLUS); break;
			case ';': addToken(SEMICOLON); break;
			case '*': addToken(STAR); break;
			case '!':
				addToken(match('=') ? BANG_EQUAL : BANG);
				break;
			case '=':
				addToken(match('=') ? EQUAL_EQUAL : EQUAL);
				break;
			case '<':
				addToken(match('=') ? LESS_EQUAL : LESS);
				break;
			case '>':
				addToken(match('=') ? GREATER_EQUAL : GREATER);
				break;
			case '/':
				if (match('/')) {
					// A comment goes until the end of the line.
					// addToken() is not called, therefore comments are completely ignored.
					while (peek() != '\n' && !isAtEnd()) advance();
				} else {
					addToken(SLASH);
				}
				break;
			case ' ':  // ignore whitespace
			case '\r': // ignore carriage return
			case '\t': // ignore tab
				// Ignore whitespace.
				break;
			case '\n': // newline is mostly ignored, but it does increment the line number
				line++;
				break;
			case '"': string(); break;
			// TO DO:
			// As it stands, this will throw an error for every invalid character.
			// If user pastes in a big blob of invalid characters, this will shotgun
			// the user with a blast of errors. Should work on coalescing all invalid
			// characters into a single error.
			default:
				Lox.error(line, "Unexpected character.");
				break;
		}
	}

	// Consumes the next character in a given source file, returns it and advances the pointer
	private char advance() {
		return source.charAt(current++);
	}

	private void addToken(TokenType type) {
		addToken(type, null);
	}

	 //Grabs the completed text from a lexeme and creates a new token for it.
	private void addToken(TokenType type, Object literal) {
		String text = source.substring(start, current);
		tokens.add(new Token(type, text, literal, line));
	}

	/**
	 * Helps determine if the current lexeme is a single character or part of a two character token.
	 * E.g., if the scanner comes across a "!", we know it starts with "!", then we look at the next
	 * character to determine if it's a "!" or "!="
	 */
	private boolean match(char expected) {
		if (isAtEnd()) return false;
		if (source.charAt(current) != expected) return false;

		current++;
		return true;
	}

	// Like advance, but does not consume the character. Sometimes referred to as lookahead.
	private char peek() {
		if (isAtEnd()) return '\0';
		return source.charAt(current);
	}

	private void string() {

	}
}
