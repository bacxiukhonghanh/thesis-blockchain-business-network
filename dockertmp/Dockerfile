FROM hyperledger/composer-rest-server
RUN npm install --production loopback-connector-mongodb passport-auth0 && \
npm cache clean --force && \
ln -s node_modules .node_modules
