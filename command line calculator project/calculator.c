#include <stdio.h>
#include <stdlib.h>
#include <ctype.h>
#include <math.h>
#include <string.h>

// Token types
typedef enum {
    INTEGER,
    PLUS,
    MINUS,
    MULTIPLY,
    DIVIDE,
    LPAREN,
    RPAREN,
    POWER,
    SQRT,
    SIN,
    COS,
    TAN,
    EOF_TOKEN
} TokenType;

// Token structure
typedef struct {
    TokenType type;
    double value;
} Token;

// Function to get the next token from input
Token getNextToken(char **input) {
    while (isspace(**input)) {
        (*input)++;
    }

    if (**input == '\0') {
        return (Token) {EOF_TOKEN, 0};
    }

    if (**input == '+') {
        (*input)++;
        return (Token) {PLUS, 0};
    }

    if (**input == '-') {
        (*input)++;
        return (Token) {MINUS, 0};
    }

    if (**input == '*') {
        (*input)++;
        return (Token) {MULTIPLY, 0};
    }

    if (**input == '/') {
        (*input)++;
        return (Token) {DIVIDE, 0};
    }

    if (**input == '^') {
        (*input)++;
        return (Token) {POWER, 0};
    }

    if (strncmp(*input, "sqrt", 4) == 0) {
        *input += 4;
        return (Token) {SQRT, 0};
    }

    if (strncmp(*input, "sin", 3) == 0) {
        *input += 3;
        return (Token) {SIN, 0};
    }

    if (strncmp(*input, "cos", 3) == 0) {
        *input += 3;
        return (Token) {COS, 0};
    }

    if (strncmp(*input, "tan", 3) == 0) {
        *input += 3;
        return (Token) {TAN, 0};
    }

    if (**input == '(') {
        (*input)++;
        return (Token) {LPAREN, 0};
    }

    if (**input == ')') {
        (*input)++;
        return (Token) {RPAREN, 0};
    }

    if (isdigit(**input) || **input == '.') {
        double value = 0;
        int decimal_place = 0;
        while (isdigit(**input) || **input == '.') {
            if (**input == '.') {
                decimal_place = 1;
                (*input)++;
                continue;
            }
            value = value * 10 + (**input - '0');
            if (decimal_place) {
                decimal_place *= 10;
            }
            (*input)++;
        }
        if (decimal_place) {
            value /= decimal_place;
        }
        return (Token) {INTEGER, value};
    }

    // Return an error token instead of exiting
    return (Token) {EOF_TOKEN, 0}; // Use EOF_TOKEN to indicate error
}


// Function to evaluate expression recursively
double evaluateExpression(char **input);

// Function to evaluate a factor
double evaluateFactor(char **input) {
    Token token = getNextToken(input);
    double result;

    if (token.type == INTEGER) {
        result = token.value;
    } else if (token.type == LPAREN) {
        result = evaluateExpression(input);
        token = getNextToken(input);
        if (token.type != RPAREN) {
            printf("Expected closing parenthesis\n");
            return NAN; // Return NaN to indicate error
        }
    } else if (token.type == MINUS) {
        result = -evaluateFactor(input);
    } else if (token.type == SQRT) {
        result = sqrt(evaluateFactor(input));
    } else if (token.type == SIN) {
        result = sin(evaluateFactor(input) * M_PI / 180);
    } else if (token.type == COS) {
        result = cos(evaluateFactor(input) * M_PI / 180);
    } else if (token.type == TAN) {
        result = tan(evaluateFactor(input) * M_PI / 180);
    } else {
        printf("Invalid factor\n");
        return NAN; // Return NaN to indicate error
    }

    return result;
}


// Function to evaluate a term
double evaluateTerm(char **input) {
    double result = evaluateFactor(input);
    if (isnan(result)) return result; // Check for error

    while (1) {
        Token token = getNextToken(input);
        if (token.type == MULTIPLY) {
            double factor = evaluateFactor(input);
            if (isnan(factor)) return factor; // Check for error
            result *= factor;
        } else if (token.type == DIVIDE) {
            double divisor = evaluateFactor(input);
            if (isnan(divisor)) return divisor; // Check for error
            if (divisor == 0) {
                printf("Division by zero\n");
                return NAN; // Return NaN to indicate error
            }
            result /= divisor;
        } else if (token.type == POWER) {
            double exponent = evaluateFactor(input);
            if (isnan(exponent)) return exponent; // Check for error
            result = pow(result, exponent);
        } else {
            (*input)--;
            break;
        }
    }

    return result;
}


// Function to evaluate an expression
double evaluateExpression(char **input) {
    double result = evaluateTerm(input);
    if (isnan(result)) return result; // Check for error

    while (1) {
        Token token = getNextToken(input);
        if (token.type == PLUS) {
            double term = evaluateTerm(input);
            if (isnan(term)) return term; // Check for error
            result += term;
        } else if (token.type == MINUS) {
            double term = evaluateTerm(input);
            if (isnan(term)) return term; // Check for error
            result -= term;
        } else {
            (*input)--;
            break;
        }
    }

    return result;
}


int main() {
    char expression[100];

    while (1) {
        printf("Enter expression (or 'end' to quit): ");
        fgets(expression, sizeof(expression), stdin);
        expression[strcspn(expression, "\n")] = '\0'; // Remove the newline character

        if (strcmp(expression, "end") == 0) {
            break;
        }

        char *input = expression;
        double result = evaluateExpression(&input);
        if (isnan(result)) {
            printf("Invalid input. Please try again.\n");
        } else {
            printf("Result: %.4f\n", result);
        }
    }

    return 0;
}

