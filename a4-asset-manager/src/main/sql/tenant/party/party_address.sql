CREATE TABLE party_address(
  party_id bigint
 ,address_id bigint
 ,CONSTRAINT party_address_pk PRIMARY KEY(party_id, address_id)
 ,CONSTRAINT party_address_party_fk FOREIGN KEY(party_id) REFERENCES party(id)
 ,CONSTRAINT party_address_address_fk FOREIGN KEY(address_id) REFERENCES address(id)
);

COMMENT ON TABLE party_address IS 'Associate an address with a party.';
