CREATE TABLE SHOW_SETUP(
    SHOW_NO VARCHAR(30) PRIMARY KEY NOT NULL,
    ROW_PER_SHOW INT NOT NULL,
    ROW_PER_SEAT INT NOT NULL,
    CNCL_WD_MIN INT NOT NULL,
    CREATED_DT TIMESTAMP WITH TIME ZONE,
    VERSION INT
);

CREATE TABLE SHOW_SEAT(
    SEAT_ID INT PRIMARY KEY,
    SHOW_NO VARCHAR(10) NOT NULL,
    SEAT_NO VARCHAR(3),
    VERSION INT,
    CONSTRAINT FK_SHOW_NO_ST FOREIGN KEY (SHOW_NO) REFERENCES SHOW_SETUP(SHOW_NO)
);

CREATE TABLE BOOKING(
    BOOK_NO UUID PRIMARY KEY NOT NULL,
    PHONE_NO VARCHAR(16) NOT NULL,
    SHOW_NO VARCHAR(10) NOT NULL,
    BOOKED_DT TIMESTAMP WITH TIME ZONE,
    VERSION INT,
    CONSTRAINT FK_SHOW_NO_BK FOREIGN KEY (SHOW_NO) REFERENCES SHOW_SETUP(SHOW_NO)
);

CREATE TABLE TICKET(
    ID INT PRIMARY KEY NOT NULL,
    TICKET_NO VARCHAR(30) ,
    BOOK_NO UUID NOT NULL,
    SEAT_ID INT,
    CANCELLED_DT TIMESTAMP WITH TIME ZONE,
    VERSION INT,
    CONSTRAINT FK_BOOK_NO_TK FOREIGN KEY (BOOK_NO) REFERENCES BOOKING(BOOK_NO),
    CONSTRAINT FK_SEAT_ID_TK FOREIGN KEY (SEAT_ID) REFERENCES SHOW_SEAT(SEAT_ID)
);

 CREATE SEQUENCE HIBERNATE_SEQUENCE START WITH 1 increment 1;


