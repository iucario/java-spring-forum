--
-- PostgreSQL database dump
--

-- Dumped from database version 14.5 (Debian 14.5-1.pgdg110+1)
-- Dumped by pg_dump version 14.5 (Ubuntu 14.5-1.pgdg20.04+1)

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- Name: comment_seq; Type: SEQUENCE; Schema: public; Owner: myuser
--

CREATE SEQUENCE public.comment_seq
    START WITH 1
    INCREMENT BY 50
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.comment_seq OWNER TO myuser;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: comments; Type: TABLE; Schema: public; Owner: myuser
--

CREATE TABLE public.comments (
    id bigint NOT NULL,
    body text,
    created_at bigint,
    updated_at bigint,
    post_id bigint,
    user_id bigint
);


ALTER TABLE public.comments OWNER TO myuser;

--
-- Name: fav_user_post; Type: TABLE; Schema: public; Owner: myuser
--

CREATE TABLE public.fav_user_post (
    id bigint NOT NULL,
    post_id bigint,
    user_id bigint
);


ALTER TABLE public.fav_user_post OWNER TO myuser;

--
-- Name: fav_user_post_seq; Type: SEQUENCE; Schema: public; Owner: myuser
--

CREATE SEQUENCE public.fav_user_post_seq
    START WITH 1
    INCREMENT BY 50
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.fav_user_post_seq OWNER TO myuser;

--
-- Name: file_seq; Type: SEQUENCE; Schema: public; Owner: myuser
--

CREATE SEQUENCE public.file_seq
    START WITH 1
    INCREMENT BY 50
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.file_seq OWNER TO myuser;

--
-- Name: files; Type: TABLE; Schema: public; Owner: myuser
--

CREATE TABLE public.files (
    id bigint NOT NULL,
    created_at bigint,
    name character varying(255),
    url character varying(255),
    user_id bigint
);


ALTER TABLE public.files OWNER TO myuser;

--
-- Name: post_seq; Type: SEQUENCE; Schema: public; Owner: myuser
--

CREATE SEQUENCE public.post_seq
    START WITH 1
    INCREMENT BY 50
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.post_seq OWNER TO myuser;

--
-- Name: posts; Type: TABLE; Schema: public; Owner: myuser
--

CREATE TABLE public.posts (
    id bigint NOT NULL,
    active_at bigint,
    body text,
    created_at bigint,
    title character varying(255),
    updated_at bigint,
    user_id bigint
);


ALTER TABLE public.posts OWNER TO myuser;

--
-- Name: user_seq; Type: SEQUENCE; Schema: public; Owner: myuser
--

CREATE SEQUENCE public.user_seq
    START WITH 1
    INCREMENT BY 50
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.user_seq OWNER TO myuser;

--
-- Name: user_stats; Type: TABLE; Schema: public; Owner: myuser
--

CREATE TABLE public.user_stats (
    id bigint NOT NULL,
    comment_count bigint,
    file_count bigint,
    post_count bigint,
    user_id bigint
);


ALTER TABLE public.user_stats OWNER TO myuser;

--
-- Name: user_stats_seq; Type: SEQUENCE; Schema: public; Owner: myuser
--

CREATE SEQUENCE public.user_stats_seq
    START WITH 1
    INCREMENT BY 50
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.user_stats_seq OWNER TO myuser;

--
-- Name: users; Type: TABLE; Schema: public; Owner: myuser
--

CREATE TABLE public.users (
    id bigint NOT NULL,
    created_at bigint,
    hashed_password character varying(255),
    name character varying(255)
);


ALTER TABLE public.users OWNER TO myuser;

--
-- Name: comments comments_pkey; Type: CONSTRAINT; Schema: public; Owner: myuser
--

ALTER TABLE ONLY public.comments
    ADD CONSTRAINT comments_pkey PRIMARY KEY (id);


--
-- Name: fav_user_post fav_user_post_pkey; Type: CONSTRAINT; Schema: public; Owner: myuser
--

ALTER TABLE ONLY public.fav_user_post
    ADD CONSTRAINT fav_user_post_pkey PRIMARY KEY (id);


--
-- Name: files files_pkey; Type: CONSTRAINT; Schema: public; Owner: myuser
--

ALTER TABLE ONLY public.files
    ADD CONSTRAINT files_pkey PRIMARY KEY (id);


--
-- Name: posts posts_pkey; Type: CONSTRAINT; Schema: public; Owner: myuser
--

ALTER TABLE ONLY public.posts
    ADD CONSTRAINT posts_pkey PRIMARY KEY (id);


--
-- Name: users uk_3g1j96g94xpk3lpxl2qbl985x; Type: CONSTRAINT; Schema: public; Owner: myuser
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT uk_3g1j96g94xpk3lpxl2qbl985x UNIQUE (name);


--
-- Name: fav_user_post ukhxv7ao8rrjsg2pcuwgmwdvxxu; Type: CONSTRAINT; Schema: public; Owner: myuser
--

ALTER TABLE ONLY public.fav_user_post
    ADD CONSTRAINT ukhxv7ao8rrjsg2pcuwgmwdvxxu UNIQUE (user_id, post_id);


--
-- Name: user_stats user_stats_pkey; Type: CONSTRAINT; Schema: public; Owner: myuser
--

ALTER TABLE ONLY public.user_stats
    ADD CONSTRAINT user_stats_pkey PRIMARY KEY (id);


--
-- Name: users users_pkey; Type: CONSTRAINT; Schema: public; Owner: myuser
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_pkey PRIMARY KEY (id);


--
-- Name: posts fk5lidm6cqbc7u4xhqpxm898qme; Type: FK CONSTRAINT; Schema: public; Owner: myuser
--

ALTER TABLE ONLY public.posts
    ADD CONSTRAINT fk5lidm6cqbc7u4xhqpxm898qme FOREIGN KEY (user_id) REFERENCES public.users(id);


--
-- Name: fav_user_post fk6v7jugcdugcmtfyhd7sdxg69c; Type: FK CONSTRAINT; Schema: public; Owner: myuser
--

ALTER TABLE ONLY public.fav_user_post
    ADD CONSTRAINT fk6v7jugcdugcmtfyhd7sdxg69c FOREIGN KEY (post_id) REFERENCES public.posts(id);


--
-- Name: comments fk8omq0tc18jd43bu5tjh6jvraq; Type: FK CONSTRAINT; Schema: public; Owner: myuser
--

ALTER TABLE ONLY public.comments
    ADD CONSTRAINT fk8omq0tc18jd43bu5tjh6jvraq FOREIGN KEY (user_id) REFERENCES public.users(id);


--
-- Name: files fkdgr5hx49828s5vhjo1s8q3wdp; Type: FK CONSTRAINT; Schema: public; Owner: myuser
--

ALTER TABLE ONLY public.files
    ADD CONSTRAINT fkdgr5hx49828s5vhjo1s8q3wdp FOREIGN KEY (user_id) REFERENCES public.users(id);


--
-- Name: comments fkh4c7lvsc298whoyd4w9ta25cr; Type: FK CONSTRAINT; Schema: public; Owner: myuser
--

ALTER TABLE ONLY public.comments
    ADD CONSTRAINT fkh4c7lvsc298whoyd4w9ta25cr FOREIGN KEY (post_id) REFERENCES public.posts(id);


--
-- Name: user_stats fkj277c5rcqlsvwkk3hj39e2b74; Type: FK CONSTRAINT; Schema: public; Owner: myuser
--

ALTER TABLE ONLY public.user_stats
    ADD CONSTRAINT fkj277c5rcqlsvwkk3hj39e2b74 FOREIGN KEY (user_id) REFERENCES public.users(id);


--
-- Name: fav_user_post fklwy7aqhtmb7rrcxymtoovci59; Type: FK CONSTRAINT; Schema: public; Owner: myuser
--

ALTER TABLE ONLY public.fav_user_post
    ADD CONSTRAINT fklwy7aqhtmb7rrcxymtoovci59 FOREIGN KEY (user_id) REFERENCES public.users(id);


--
-- PostgreSQL database dump complete
--

