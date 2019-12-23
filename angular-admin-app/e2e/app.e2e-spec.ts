/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import { AngularTestPage } from './app.po';
import { ExpectedConditions, browser, element, by } from 'protractor';
import {} from 'jasmine';


describe('Starting tests for angular-admin-app', function() {
  let page: AngularTestPage;

  beforeEach(() => {
    page = new AngularTestPage();
  });

  it('website title should be angular-admin-app', () => {
    page.navigateTo('/');
    return browser.getTitle().then((result)=>{
      expect(result).toBe('angular-admin-app');
    })
  });

  it('network-name should be tutorial-network@0.0.14',() => {
    element(by.css('.network-name')).getWebElement()
    .then((webElement) => {
      return webElement.getText();
    })
    .then((txt) => {
      expect(txt).toBe('tutorial-network@0.0.14.bna');
    });
  });

  it('navbar-brand should be angular-admin-app',() => {
    element(by.css('.navbar-brand')).getWebElement()
    .then((webElement) => {
      return webElement.getText();
    })
    .then((txt) => {
      expect(txt).toBe('angular-admin-app');
    });
  });

  
    it('Parcel component should be loadable',() => {
      page.navigateTo('/Parcel');
      browser.findElement(by.id('assetName'))
      .then((assetName) => {
        return assetName.getText();
      })
      .then((txt) => {
        expect(txt).toBe('Parcel');
      });
    });

    it('Parcel table should have 8 columns',() => {
      page.navigateTo('/Parcel');
      element.all(by.css('.thead-cols th')).then(function(arr) {
        expect(arr.length).toEqual(8); // Addition of 1 for 'Action' column
      });
    });
  
    it('Unit component should be loadable',() => {
      page.navigateTo('/Unit');
      browser.findElement(by.id('assetName'))
      .then((assetName) => {
        return assetName.getText();
      })
      .then((txt) => {
        expect(txt).toBe('Unit');
      });
    });

    it('Unit table should have 9 columns',() => {
      page.navigateTo('/Unit');
      element.all(by.css('.thead-cols th')).then(function(arr) {
        expect(arr.length).toEqual(9); // Addition of 1 for 'Action' column
      });
    });
  

  
    it('Cooperative component should be loadable',() => {
      page.navigateTo('/Cooperative');
      browser.findElement(by.id('participantName'))
      .then((participantName) => {
        return participantName.getText();
      })
      .then((txt) => {
        expect(txt).toBe('Cooperative');
      });
    });

    it('Cooperative table should have 7 columns',() => {
      page.navigateTo('/Cooperative');
      element.all(by.css('.thead-cols th')).then(function(arr) {
        expect(arr.length).toEqual(7); // Addition of 1 for 'Action' column
      });
    });
  
    it('TransportComp component should be loadable',() => {
      page.navigateTo('/TransportComp');
      browser.findElement(by.id('participantName'))
      .then((participantName) => {
        return participantName.getText();
      })
      .then((txt) => {
        expect(txt).toBe('TransportComp');
      });
    });

    it('TransportComp table should have 7 columns',() => {
      page.navigateTo('/TransportComp');
      element.all(by.css('.thead-cols th')).then(function(arr) {
        expect(arr.length).toEqual(7); // Addition of 1 for 'Action' column
      });
    });
  
    it('Seller component should be loadable',() => {
      page.navigateTo('/Seller');
      browser.findElement(by.id('participantName'))
      .then((participantName) => {
        return participantName.getText();
      })
      .then((txt) => {
        expect(txt).toBe('Seller');
      });
    });

    it('Seller table should have 7 columns',() => {
      page.navigateTo('/Seller');
      element.all(by.css('.thead-cols th')).then(function(arr) {
        expect(arr.length).toEqual(7); // Addition of 1 for 'Action' column
      });
    });
  
    it('Guest component should be loadable',() => {
      page.navigateTo('/Guest');
      browser.findElement(by.id('participantName'))
      .then((participantName) => {
        return participantName.getText();
      })
      .then((txt) => {
        expect(txt).toBe('Guest');
      });
    });

    it('Guest table should have 2 columns',() => {
      page.navigateTo('/Guest');
      element.all(by.css('.thead-cols th')).then(function(arr) {
        expect(arr.length).toEqual(2); // Addition of 1 for 'Action' column
      });
    });
  

  
    it('AddUnitToParcel component should be loadable',() => {
      page.navigateTo('/AddUnitToParcel');
      browser.findElement(by.id('transactionName'))
      .then((transactionName) => {
        return transactionName.getText();
      })
      .then((txt) => {
        expect(txt).toBe('AddUnitToParcel');
      });
    });
  
    it('Trade component should be loadable',() => {
      page.navigateTo('/Trade');
      browser.findElement(by.id('transactionName'))
      .then((transactionName) => {
        return transactionName.getText();
      })
      .then((txt) => {
        expect(txt).toBe('Trade');
      });
    });
  
    it('PutParcelIntoStock component should be loadable',() => {
      page.navigateTo('/PutParcelIntoStock');
      browser.findElement(by.id('transactionName'))
      .then((transactionName) => {
        return transactionName.getText();
      })
      .then((txt) => {
        expect(txt).toBe('PutParcelIntoStock');
      });
    });
  
    it('ForSale component should be loadable',() => {
      page.navigateTo('/ForSale');
      browser.findElement(by.id('transactionName'))
      .then((transactionName) => {
        return transactionName.getText();
      })
      .then((txt) => {
        expect(txt).toBe('ForSale');
      });
    });
  
    it('Sold component should be loadable',() => {
      page.navigateTo('/Sold');
      browser.findElement(by.id('transactionName'))
      .then((transactionName) => {
        return transactionName.getText();
      })
      .then((txt) => {
        expect(txt).toBe('Sold');
      });
    });
  

});