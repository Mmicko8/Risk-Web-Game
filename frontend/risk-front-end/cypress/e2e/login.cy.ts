/// <reference types="cypress" />

// @ts-ignore
describe('Login to the page (assuming the backend runs and has correct test data)', () => {
    it('Should log in the user, fill the local storage and redirect to sign in ', () => {
        cy.visit('http://localhost:3000/sign_in')
        cy.get('#username').type('KdgUser1').should('have.value', 'KdgUser1')
        cy.get('#password').type('password').should('have.value', 'password')
        cy.contains('Sign In').click()
        cy.url().should('equal', 'http://localhost:3000/')
    });
    it('Should give error message, when using wrong credentials', () => {
        cy.visit('http://localhost:3000/sign_in')
        cy.get('#username').type('KdgUser1').should('have.value', 'KdgUser1')
        cy.get('#password').type('passw').should('have.value', 'passw')
        cy.contains('Sign In').click()
        cy.contains('The username or password was incorrect')
    });
})