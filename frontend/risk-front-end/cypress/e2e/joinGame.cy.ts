/// <reference types="cypress" />

// @ts-ignore
before(() => {
    cy.visit('http://localhost:3000/sign_in')
    cy.get('#username').type('KdgUser1').should('have.value', 'KdgUser1')
    cy.get('#password').type('password').should('have.value', 'password')
    cy.contains('Sign In').click()
    cy.url().should('equal', 'http://localhost:3000/')
})

describe('Joining a game (assuming the backend runs and has correct test data)', () => {
    it('Should show the game', () => {
        // cy.get('[data-testid=LogoutIcon]').click() //-> logout?
        cy.visit('http://localhost:3000/')
        cy.get('[aria-label=createLobby]').click()
        cy.get('[data-testid=CreateLobbySubmit]').click()
        cy.get('[data-testid=AddAi]').click().click()
        cy.get('[data-testid=StartGame]').click()
    });
})