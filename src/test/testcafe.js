import {RequestHook} from 'testcafe';

const {Selector} = require('testcafe');

const formatDate = (date) => {
   const year = date.getFullYear();
   const month = String(date.getMonth() + 1).padStart(2, '0');
   const day = String(date.getDate()).padStart(2, '0');
   return `${year}-${month}-${day}`;
};

class JwtBearerAuthorization extends RequestHook {
   constructor() {
      super();
      // Retrieve token from environment variable
      this.token = process.env.test_jwt_bearer;
   }

   async onRequest(event) {
      event.requestOptions.headers['Authorization'] = this.token;
   }

   async onResponse(event) {
   }
}

// Create an instance of the hook
const authHeaderHook = new JwtBearerAuthorization();

fixture('Tests')
    .page('http://localhost:4200/')
    .requestHooks(authHeaderHook);

test('Create a profile', async test => {
   await test
       // Access the profiles page, and then open the modal for creating a new profile
      .click('#profilePage')
      .wait(2000)
      .click('#createProfileButton')

       // Fill out the form
      .typeText('#nameField', 'Test profile E2E')
      .click('#geographyMenu')
      .click('#Denmark')
      .click('#productionSelector')
      .typeText('#annualCost', '60000')
      .typeText('#annualHours', '2080')
      .typeText('#effectiveness', '80')
      .typeText('#hoursPerDay', '8')
      .click('#addButton')
       .wait(1000);

   await test.typeText('#searchField', 'Test profile E2E');

   // Assertion to verify the profile is added in the table
   const profileRow = Selector('.profilesTable').find('td').withText('Test profile E2E');
   await test.expect(profileRow.exists).ok('The profile was not found in the table');
});

test('create team', async test => {

   // Access teams page and open modal for creating teams
   await test
       .click('#teamsPage')
       .wait(2000);
   await test.click('#openCreateTeam');

   // Get profile in list, then fill name of team and select profile
   const profileToSelect = Selector('mat-list-option').withText('Test profile E2E');
   await test
       .typeText('#teamNameField', 'Test Team E2E')
       .click(profileToSelect);

   // Fill allocation percentages for profile
   const costInput = Selector('input').withAttribute('formcontrolname', 'allocationPercentageCost');
   const hourInput = Selector('input').withAttribute('formcontrolname', 'allocationPercentageHours');
   await test
       .typeText(costInput, '100')
       .typeText(hourInput, '100');

   // Create team
   await test.click('#addButton').wait(1000);

   await test.typeText('#searchField', 'Test Team E2E');

   // Assertion to verify the team is added in the table
   const teamRow = Selector('.teamsTable').find('td').withText('Test Team E2E');
   await test.expect(teamRow.exists).ok('The profile was not found in the table');
});

test('Create a project', async test => {
   // Prep. calculating start and end date for project
   const today = new Date();
   const startDate = new Date(today);
   startDate.setDate(today.getDate() + 10); // Start date: Today + 10 days

   const endDate = new Date(startDate);
   endDate.setDate(startDate.getDate() + 14); // End date: Start date + 14 days

   const formattedStartDate = formatDate(startDate);
   const formattedEndDate = formatDate(endDate);

    // Access projects page and open modal for creating projects
    await test
         .click('#projectsPage')
         .wait(2000)
         .click('#openCreateProject');

    // Fill out the form
   const startDateInput = Selector('input[matStartDate]');
   const endDateInput = Selector('input[matEndDate]');
    await test
        .typeText('#projectNameField', 'Test project E2E')
        .typeText('#salesNumberField', '838383')
        .typeText('#totalPriceField', '40000')
        .typeText(startDateInput, formattedStartDate, {replace: true})
        .typeText(endDateInput, formattedEndDate, {replace: true})
        .click('#geographyMenu')
        .click('#Denmark');

    // Select team and add allocation
   const teamToSelect = Selector('mat-list-option').withText('Test Team E2E');
   await test
       .click(teamToSelect);
   const allocationInput = Selector('input').withAttribute('formcontrolname', 'allocation');
   await test
       .typeText(allocationInput, '100');

   await test.click('#addButton').wait(1000);

   await test.typeText('#searchField', 'Test project E2E');

   // Assertion to verify the project is added in the table
   const projectRow = Selector('.projectsTable').find('td').withText('Test project E2E');
   await test.expect(projectRow.exists).ok('The project was not found in the table');
});

test('Edit profile and see effect on project', async test => {
   // Go to profile page, find and select profile to edit
   await test
       .click('#profilePage')
       .wait(2000)
       .typeText('#searchField', 'Test profile E2E');

   // Find and click on the kebab menu for options on the row and choose more
   const profileRow = Selector('.profilesTable').find('tr').withText('Test profile E2E');
   const options = profileRow.find('button[mat-icon-button]')
   await test
       .click(options);
   const moreButton = Selector('button').withText('More');
   await test.click(moreButton).wait(1000)

   // Change annual cost of profile and save
   const annualCostField = Selector('input').withAttribute('formcontrolname', 'annual_cost');
   await test
       .typeText(annualCostField, '80000', {replace: true})
       .click('#updateButton')
       .wait(2000);

   // Navigate to projects page and search for project
   await test
       .click('#projectsPage')
       .wait(2000)
       .typeText('#searchField', 'Test project E2E');

   // Find day rate on project and assert it has changed to correct value
   const projectRow = Selector('.projectsTable').find('td').withText('307.68');
   await test.expect(projectRow.exists).ok('The project was not found in the table');
});

test('Delete a Project', async test => {
   // Access project page and search for project
   await test
       .click('#projectsPage')
       .wait(2000)
       .typeText('#searchField', 'Test project E2E');

   // Find and click on the kebab menu for options on the row
   const projectRow = Selector('.projectsTable').find('tr').withText('Test project E2E');
   const options = projectRow.find('button[mat-icon-button]')
   await test
       .click(options);

   // Delete project
   const deleteButton = Selector('button').withText('Delete');
   await test
       .click(deleteButton);
   const confirm = Selector('button').withText('Confirm');
   await test.click(confirm);

   // Assert its gone
   await test
       .expect(projectRow.exists)
       .notOk('The profile still exists after attempting to delete it')
})

test('Delete a team', async test => {
   // Access teams page and search for team
   await test
       .click('#teamsPage')
       .wait(2000)
       .typeText('#searchField', 'Test Team E2E');

   // Find and click on the kebab menu for options on the row
   const teamRow = Selector('.teamsTable').find('tr').withText('Test Team E2E');
   const options = teamRow.find('button[mat-icon-button]')
   await test
       .click(options);

   // Delete team
   const deleteButton = Selector('button').withText('Delete');
   await test
       .click(deleteButton);
   const confirm = Selector('button').withText('Confirm');
   await test.click(confirm);

   // Assert its gone
   await test
       .expect(teamRow.exists)
       .notOk('The profile still exists after attempting to delete it')
})

test('Delete a profile', async test => {
   // Access profiles page, use search to find profile created in previous test
   await test
       .click('#profilePage')
       .wait(2000)
       .typeText('#searchField', 'Test profile E2E');

   // Find and click on the kebab menu for options on the row
   const profileRow = Selector('.profilesTable').find('tr').withText('Test profile E2E');
   const options = profileRow.find('button[mat-icon-button]')
   await test
       .click(options);

   // Delete profile
   const deleteButton = Selector('button').withText('Delete');
   await test
       .click(deleteButton);
   const confirm = Selector('button').withText('Confirm');
   await test.click(confirm);

   // Assert its gone
   await test
       .expect(profileRow.exists)
       .notOk('The profile still exists after attempting to delete it')
});
