///*
// * Copyright (C) 2011 The Android Open Source Project
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *      http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//
//package com.android.ex.chips;
//
//import android.accounts.Account;
//import android.content.ContentResolver;
//import android.content.Context;
//import android.content.pm.PackageManager;
//import android.content.pm.PackageManager.NameNotFoundException;
//import android.content.res.Resources;
//import android.database.Cursor;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.net.Uri;
//import android.os.AsyncTask;
//import android.os.Handler;
//import android.os.Message;
//import android.provider.ContactsContract;
//import android.provider.ContactsContract.CommonDataKinds.Photo;
//import android.provider.ContactsContract.Directory;
//import android.support.v4.util.LruCache;
//import android.text.TextUtils;
//import android.text.util.Rfc822Token;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.AutoCompleteTextView;
//import android.widget.BaseAdapter;
//import android.widget.Filter;
//import android.widget.Filterable;
//
//import com.android.ex.chips.DropdownChipLayouter.AdapterType;
//
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.io.ByteArrayOutputStream;
//import java.io.FileNotFoundException;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.InputStreamReader;
//import java.net.HttpURLConnection;
//import java.net.MalformedURLException;
//import java.net.URL;
//import java.net.URLEncoder;
//import java.util.ArrayList;
//import java.util.HashSet;
//import java.util.LinkedHashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.Set;
//
///**
// * Adapter for showing a recipient list.
// */
//public class HashTagAdapter extends BaseAdapter implements Filterable, AccountSpecifier {
//    private static final String TAG = "BaseRecipientAdapter";
//    private static final String API_BASE = "http://api.justword.com/index.php";
//    private static final boolean DEBUG = true;
//    JSONArray hashTagJsonArray;
//
//    /*
//     * @param mEntries Temp result of hashtag
//     * @param newEntries up to date result of hashtag
//     */
//    ArrayList<HashTag> mEntries;
//    ArrayList<HashTag> newEntries;
//
//    /**
//     * The preferred number of results to be retrieved. This number may be
//     * exceeded if there are several directories configured, because we will use
//     * the same limit for all directories.
//     */
//    private static final int DEFAULT_PREFERRED_MAX_RESULT_COUNT = 10;
//
//    /**
//     * The number of extra entries requested to allow for duplicates. Duplicates
//     * are removed from the overall result.
//     */
//    static final int ALLOWANCE_FOR_DUPLICATES = 5;
//
//
//    /** The number of photos cached in this Adapter. */
//    private static final int PHOTO_CACHE_SIZE = 200;
//
//    /**
//     * The "Waiting for more contacts" message will be displayed if search is not complete
//     * within this many milliseconds.
//     */
//    private static final int MESSAGE_SEARCH_PENDING_DELAY = 1000;
//    /** Used to prepare "Waiting for more contacts" message. */
//    private static final int MESSAGE_SEARCH_PENDING = 1;
//
//
//    /**
//     * Used to pass results from {@link DefaultFilter#performFiltering(CharSequence)} to
//     * {@link DefaultFilter#publishResults(CharSequence, Filter.FilterResults)}
//     */
//    private static class DefaultFilterResult {
//        public final ArrayList<HashTag> entries;
//
//        public DefaultFilterResult(ArrayList<HashTag> entries) {
//            this.entries  = entries;
//        }
//    }
//
//    /**
//     * An asynchronous filter used for loading two data sets: email rows from the local
//     * contact provider and the list of {@link Directory}'s.
//     */
//    private final class DefaultFilter extends Filter {
//
//        @Override
//        protected FilterResults performFiltering(CharSequence constraint) {
//            if (DEBUG) {
//                Log.d(TAG, "start filtering. constraint: " + constraint + ", thread:"
//                        + Thread.currentThread());
//            }
//
//            if (constraint == null) {
//                return new FilterResults();
//            }
//
//            final FilterResults results = new FilterResults();
//            boolean limitResults = true;
//
//            if (TextUtils.isEmpty(constraint)) {
//                limitResults = false;
//            }
//
//            try {
//                if(limitResults){
//                    ArrayList<HashTag> hashTagList = autoComplete(constraint.toString());
//
//                    if (hashTagList == null) {
//                        if (DEBUG) {
//                            Log.w(TAG, "null cursor returned for default Email filter query.");
//                        }
//                    } else {
//                        final Set<String> existingDestinations = new HashSet<String>();
//                        results.values = new DefaultFilterResult(hashTagList);
//                        results.count = 1;
//                    }
//                }
//            }catch (Exception e){
//                e.printStackTrace();
//            }
//            return results;
//        }
//
//        @Override
//        protected void publishResults(final CharSequence constraint, FilterResults results) {
//            // If a user types a string very quickly and database is slow, "constraint" refers to
//            // an older text which shows inconsistent results for users obsolete (b/4998713).
//            // TODO: Fix it.
//            mCurrentConstraint = constraint;
//
//            clearTempEntries();
//
//            if (results.values != null) {
//                DefaultFilterResult defaultFilterResult = (DefaultFilterResult) results.values;
//                newEntries = defaultFilterResult.entries;
//
//                // If there are no local results, in the new result set, cache off what had been
//                // shown to the user for use until the first directory result is returned
//                if (defaultFilterResult.entries.size() == 0) {
//                    cacheCurrentEntries();
//                }
//
//                updateEntries(defaultFilterResult.entries);
//
//                // We need to search other remote directories, doing other Filter requests.
//                int hashTagToGo = mPreferredMaxResultCount - defaultFilterResult.entries.size();
//                if (hashTagToGo>0){
//                    searchForHotestTag(constraint, defaultFilterResult.entries, hashTagToGo);
//                }
//            }
//
//        }
//
//        @Override
//        public CharSequence convertResultToString(Object resultValue) {
//            final RecipientEntry entry = (RecipientEntry)resultValue;
//            final String displayName = entry.getDisplayName();
//            final String emailAddress = entry.getDestination();
//            if (TextUtils.isEmpty(displayName) || TextUtils.equals(displayName, emailAddress)) {
//                 return emailAddress;
//            } else {
//                return new Rfc822Token(displayName, emailAddress, null).toString();
//            }
//        }
//    }
//
//    /**
//     * An asynchronous filter that performs search in a particular directory.
//     */
//    protected class DirectoryFilter extends Filter {
//        private final DirectorySearchParams mParams;
//        private int mLimit;
//
//        public DirectoryFilter(DirectorySearchParams params) {
//            mParams = params;
//        }
//
//        public synchronized void setLimit(int limit) {
//            this.mLimit = limit;
//        }
//
//        public synchronized int getLimit() {
//            return this.mLimit;
//        }
//
//        @Override
//        protected FilterResults performFiltering(CharSequence constraint) {
//            if (DEBUG) {
//                Log.d(TAG, "DirectoryFilter#performFiltering. directoryId: " + mParams.directoryId
//                        + ", constraint: " + constraint + ", thread: " + Thread.currentThread());
//            }
//            final FilterResults results = new FilterResults();
//            results.values = null;
//            results.count = 0;
//
//            if (!TextUtils.isEmpty(constraint)) {
//                final ArrayList<TemporaryEntry> tempEntries = new ArrayList<TemporaryEntry>();
//
//                Cursor cursor = null;
//                try {
//                    // We don't want to pass this Cursor object to UI thread (b/5017608).
//                    // Assuming the result should contain fairly small results (at most ~10),
//                    // We just copy everything to local structure.
//                    cursor = doQuery(constraint, getLimit(), mParams.directoryId);
//
//                    if (cursor != null) {
//                        while (cursor.moveToNext()) {
//                            tempEntries.add(new TemporaryEntry(cursor, mParams.directoryId));
//                        }
//                    }
//                } finally {
//                    if (cursor != null) {
//                        cursor.close();
//                    }
//                }
//                if (!tempEntries.isEmpty()) {
//                    results.values = tempEntries;
//                    results.count = 1;
//                }
//            }
//
//            if (DEBUG) {
//                Log.v(TAG, "finished loading directory \"" + mParams.displayName + "\"" +
//                        " with query " + constraint);
//            }
//
//            return results;
//        }
//
//        @Override
//        protected void publishResults(final CharSequence constraint, FilterResults results) {
//            if (DEBUG) {
//                Log.d(TAG, "DirectoryFilter#publishResult. constraint: " + constraint
//                        + ", mCurrentConstraint: " + mCurrentConstraint);
//            }
//            mDelayedMessageHandler.removeDelayedLoadMessage();
//            // Check if the received result matches the current constraint
//            // If not - the user must have continued typing after the request was issued, which
//            // means several member variables (like mRemainingDirectoryLoad) are already
//            // overwritten so shouldn't be touched here anymore.
//            if (TextUtils.equals(constraint, mCurrentConstraint)) {
//                if (results.count > 0) {
//                    @SuppressWarnings("unchecked")
//                    final ArrayList<TemporaryEntry> tempEntries =
//                            (ArrayList<TemporaryEntry>) results.values;
//
//                    for (TemporaryEntry tempEntry : tempEntries) {
//                        putOneEntry(tempEntry, mParams.directoryId == Directory.DEFAULT,
//                                mEntryMap, mNonAggregatedEntries, mExistingDestinations);
//                    }
//                }
//
//                // If there are remaining directories, set up delayed message again.
//                mRemainingDirectoryCount--;
//                if (mRemainingDirectoryCount > 0) {
//                    if (DEBUG) {
//                        Log.d(TAG, "Resend delayed load message. Current mRemainingDirectoryLoad: "
//                                + mRemainingDirectoryCount);
//                    }
//                    mDelayedMessageHandler.sendDelayedLoadMessage();
//                }
//
//                // If this directory result has some items, or there are no more directories that
//                // we are waiting for, clear the temp results
//                if (results.count > 0 || mRemainingDirectoryCount == 0) {
//                    // Clear the temp entries
//                    clearTempEntries();
//                }
//            }
//
//            // Show the list again without "waiting" message.
//            updateEntries(constructEntryList(mEntryMap, mNonAggregatedEntries));
//        }
//    }
//
//    private final Context mContext;
//    private final ContentResolver mContentResolver;
//    private final LayoutInflater mInflater;
//    private Account mAccount;
//    private final int mPreferredMaxResultCount;
//    private DropdownChipLayouter mDropdownChipLayouter;
//
//
//
//    /** The number of directories this adapter is waiting for results. */
//    private int mRemainingDirectoryCount;
//
//    /**
//     * Used to ignore asynchronous queries with a different constraint, which may happen when
//     * users type characters quickly.
//     */
//    private CharSequence mCurrentConstraint;
//
//    private static LruCache<Uri, byte[]> mPhotoCacheMap;
//
//    /**
//     * Handler specific for maintaining "Waiting for more contacts" message, which will be shown
//     * when:
//     * - there are directories to be searched
//     * - results from directories are slow to come
//     */
//    private final class DelayedMessageHandler extends Handler {
//        @Override
//        public void handleMessage(Message msg) {
//            if (mRemainingDirectoryCount > 0) {
//                updateEntries(constructEntryList(mEntryMap, mNonAggregatedEntries));
//            }
//        }
//
//        public void sendDelayedLoadMessage() {
//            sendMessageDelayed(obtainMessage(MESSAGE_SEARCH_PENDING, 0, 0, null),
//                    MESSAGE_SEARCH_PENDING_DELAY);
//        }
//
//        public void removeDelayedLoadMessage() {
//            removeMessages(MESSAGE_SEARCH_PENDING);
//        }
//    }
//
//    private final DelayedMessageHandler mDelayedMessageHandler = new DelayedMessageHandler();
//
//    private EntriesUpdatedObserver mEntriesUpdatedObserver;
//
//    /**
//     * Constructor for email queries.
//     */
//    public HashTagAdapter(Context context) {
//        this(context, DEFAULT_PREFERRED_MAX_RESULT_COUNT, QUERY_TYPE_EMAIL);
//    }
//
//    public HashTagAdapter(Context context, int preferredMaxResultCount) {
//        this(context, preferredMaxResultCount, QUERY_TYPE_EMAIL);
//    }
//
//    public HashTagAdapter(int queryMode, Context context) {
//        this(context, DEFAULT_PREFERRED_MAX_RESULT_COUNT, queryMode);
//    }
//
//    public HashTagAdapter(int queryMode, Context context, int preferredMaxResultCount) {
//        this(context, preferredMaxResultCount, queryMode);
//    }
//
//    public HashTagAdapter(Context context, int preferredMaxResultCount, int queryMode) {
//        mContext = context;
//        mContentResolver = context.getContentResolver();
//        mInflater = LayoutInflater.from(context);
//        mPreferredMaxResultCount = preferredMaxResultCount;
//        if (mPhotoCacheMap == null) {
//            mPhotoCacheMap = new LruCache<Uri, byte[]>(PHOTO_CACHE_SIZE);
//        }
//        mQueryType = queryMode;
//
//        if (queryMode == QUERY_TYPE_EMAIL) {
//            mQuery = Queries.EMAIL;
//        } else if (queryMode == QUERY_TYPE_PHONE) {
//            mQuery = Queries.PHONE;
//        } else {
//            mQuery = Queries.EMAIL;
//            Log.e(TAG, "Unsupported query type: " + queryMode);
//        }
//    }
//
//    public Context getContext() {
//        return mContext;
//    }
//
//    public int getQueryType() {
//        return mQueryType;
//    }
//
//    public void setDropdownChipLayouter(DropdownChipLayouter dropdownChipLayouter) {
//        mDropdownChipLayouter = dropdownChipLayouter;
//        mDropdownChipLayouter.setQuery(mQuery);
//    }
//
//    public DropdownChipLayouter getDropdownChipLayouter() {
//        return mDropdownChipLayouter;
//    }
//
//    /**
//     * Set the account when known. Causes the search to prioritize contacts from that account.
//     */
//    @Override
//    public void setAccount(Account account) {
//        mAccount = account;
//    }
//
//    /** Will be called from {@link AutoCompleteTextView} to prepare auto-complete list. */
//    @Override
//    public Filter getFilter() {
//        return new DefaultFilter();
//    }
//
//    /**
//     * An extesion to {@link RecipientAlternatesAdapter#getMatchingRecipients} that allows
//     * additional sources of contacts to be considered as matching recipients.
//     * @param addresses A set of addresses to be matched
//     * @return A list of matches or null if none found
//     */
//    public Map<String, RecipientEntry> getMatchingRecipients(Set<String> addresses) {
//        return null;
//    }
//
//    public static List<DirectorySearchParams> setupOtherDirectories(Context context,
//            Cursor directoryCursor, Account account) {
//        final PackageManager packageManager = context.getPackageManager();
//        final List<DirectorySearchParams> paramsList = new ArrayList<DirectorySearchParams>();
//        DirectorySearchParams preferredDirectory = null;
//        while (directoryCursor.moveToNext()) {
//            final long id = directoryCursor.getLong(DirectoryListQuery.ID);
//
//            // Skip the local invisible directory, because the default directory already includes
//            // all local results.
//            if (id == Directory.LOCAL_INVISIBLE) {
//                continue;
//            }
//
//            final DirectorySearchParams params = new DirectorySearchParams();
//            final String packageName = directoryCursor.getString(DirectoryListQuery.PACKAGE_NAME);
//            final int resourceId = directoryCursor.getInt(DirectoryListQuery.TYPE_RESOURCE_ID);
//            params.directoryId = id;
//            params.displayName = directoryCursor.getString(DirectoryListQuery.DISPLAY_NAME);
//            params.accountName = directoryCursor.getString(DirectoryListQuery.ACCOUNT_NAME);
//            params.accountType = directoryCursor.getString(DirectoryListQuery.ACCOUNT_TYPE);
//            if (packageName != null && resourceId != 0) {
//                try {
//                    final Resources resources =
//                            packageManager.getResourcesForApplication(packageName);
//                    params.directoryType = resources.getString(resourceId);
//                    if (params.directoryType == null) {
//                        Log.e(TAG, "Cannot resolve directory name: "
//                                + resourceId + "@" + packageName);
//                    }
//                } catch (NameNotFoundException e) {
//                    Log.e(TAG, "Cannot resolve directory name: "
//                            + resourceId + "@" + packageName, e);
//                }
//            }
//
//            // If an account has been provided and we found a directory that
//            // corresponds to that account, place that directory second, directly
//            // underneath the local contacts.
//            if (account != null && account.name.equals(params.accountName) &&
//                    account.type.equals(params.accountType)) {
//                preferredDirectory = params;
//            } else {
//                paramsList.add(params);
//            }
//        }
//
//        if (preferredDirectory != null) {
//            paramsList.add(1, preferredDirectory);
//        }
//
//        return paramsList;
//    }
//
//    /**
//     * Starts search in other directories using {@link Filter}. Results will be handled in
//     * {@link DirectoryFilter}.
//     */
//    protected void searchForHotestTag(
//            CharSequence constraint, ArrayList<HashTag> paramsList, int limit) {
//        // Directory search started. We may show "waiting" message if directory results are slow
//        // enough.
//        mRemainingDirectoryCount = count - 1;
//        mDelayedMessageHandler.sendDelayedLoadMessage();
//    }
//
//    private static void putOneEntry(TemporaryEntry entry, boolean isAggregatedEntry,
//            LinkedHashMap<Long, List<RecipientEntry>> entryMap,
//            List<RecipientEntry> nonAggregatedEntries,
//            Set<String> existingDestinations) {
//        if (existingDestinations.contains(entry.destination)) {
//            return;
//        }
//
//        existingDestinations.add(entry.destination);
//
//        if (!isAggregatedEntry) {
//            nonAggregatedEntries.add(RecipientEntry.constructTopLevelEntry(
//                    entry.displayName,
//                    entry.displayNameSource,
//                    entry.destination, entry.destinationType, entry.destinationLabel,
//                    entry.contactId, entry.directoryId, entry.dataId, entry.thumbnailUriString,
//                    true, entry.lookupKey));
//        } else if (entryMap.containsKey(entry.contactId)) {
//            // We already have a section for the person.
//            final List<RecipientEntry> entryList = entryMap.get(entry.contactId);
//            entryList.add(RecipientEntry.constructSecondLevelEntry(
//                    entry.displayName,
//                    entry.displayNameSource,
//                    entry.destination, entry.destinationType, entry.destinationLabel,
//                    entry.contactId, entry.directoryId, entry.dataId, entry.thumbnailUriString,
//                    true, entry.lookupKey));
//        } else {
//            final List<RecipientEntry> entryList = new ArrayList<RecipientEntry>();
//            entryList.add(RecipientEntry.constructTopLevelEntry(
//                    entry.displayName,
//                    entry.displayNameSource,
//                    entry.destination, entry.destinationType, entry.destinationLabel,
//                    entry.contactId, entry.directoryId, entry.dataId, entry.thumbnailUriString,
//                    true, entry.lookupKey));
//            entryMap.put(entry.contactId, entryList);
//        }
//    }
//
//    /**
//     * Constructs an actual list for this Adapter using {@link #mEntryMap}. Also tries to
//     * fetch a cached photo for each contact entry (other than separators), or request another
//     * thread to get one from directories.
//     */
//    private List<RecipientEntry> constructEntryList(
//            LinkedHashMap<Long, List<RecipientEntry>> entryMap,
//            List<RecipientEntry> nonAggregatedEntries) {
//        final List<RecipientEntry> entries = new ArrayList<RecipientEntry>();
//        int validEntryCount = 0;
//        for (Map.Entry<Long, List<RecipientEntry>> mapEntry : entryMap.entrySet()) {
//            final List<RecipientEntry> entryList = mapEntry.getValue();
//            final int size = entryList.size();
//            for (int i = 0; i < size; i++) {
//                RecipientEntry entry = entryList.get(i);
//                entries.add(entry);
//                tryFetchPhoto(entry, mContentResolver, this, false, i);
//                validEntryCount++;
//            }
////            if (validEntryCount > mPreferredMaxResultCount) {
////                break;
////            }
//        }
//        if (validEntryCount <= mPreferredMaxResultCount) {
//            for (int i = 0; i < nonAggregatedEntries.size(); i++) {
//                RecipientEntry entry = nonAggregatedEntries.get(i);
////                if (validEntryCount > mPreferredMaxResultCount) {
////                    break;
////                }
//                entries.add(entry);
//                tryFetchPhoto(entry, mContentResolver, this, false, i);
//
//                validEntryCount++;
//            }
//        }
//
//        return entries;
//    }
//
//
//    public interface EntriesUpdatedObserver {
//        public void onChanged(List<RecipientEntry> entries);
//    }
//
//    public void registerUpdateObserver(EntriesUpdatedObserver observer) {
//        mEntriesUpdatedObserver = observer;
//    }
//
//    private void updateEntries(ArrayList<HashTag> newEntries) {
//        mEntries = newEntries;
//        mEntriesUpdatedObserver.onChanged(newEntries);
//        notifyDataSetChanged();
//    }
//
//    private void cacheCurrentEntries() {
//        mTempEntries = mEntries;
//    }
//
//    private void clearTempEntries() {
//        mTempEntries = null;
//    }
//
//    protected List<RecipientEntry> getEntries() {
//        return mTempEntries != null ? mTempEntries : mEntries;
//    }
//
//    public static void tryFetchPhoto(final RecipientEntry entry, ContentResolver mContentResolver, BaseAdapter adapter, boolean forceLoad, int position) {
//        if (forceLoad || position <= 20) {
//            final Uri photoThumbnailUri = entry.getPhotoThumbnailUri();
//            if (photoThumbnailUri != null) {
//                final byte[] photoBytes = mPhotoCacheMap.get(photoThumbnailUri);
//                if (photoBytes != null) {
//                    entry.setPhotoBytes(photoBytes);
//                    // notifyDataSetChanged() should be called by a caller.
//                } else {
//                    if (DEBUG) {
//                        Log.d(TAG, "No photo cache for " + entry.getDisplayName()
//                                + ". Fetch one asynchronously");
//                    }
//                    fetchPhotoAsync(entry, photoThumbnailUri, adapter, mContentResolver);
//                }
//            }
//        }
//    }
//
//    // For reading photos for directory contacts, this is the chunksize for
//    // copying from the inputstream to the output stream.
//    private static final int BUFFER_SIZE = 1024*16;
//
//    private static void fetchPhotoAsync(final RecipientEntry entry, final Uri photoThumbnailUri, final BaseAdapter adapter, final ContentResolver mContentResolver) {
//        final AsyncTask<Void, Void, byte[]> photoLoadTask = new AsyncTask<Void, Void, byte[]>() {
//            @Override
//            protected byte[] doInBackground(Void... params) {
//                // First try running a query. Images for local contacts are
//                // loaded by sending a query to the ContactsProvider.
//                final Cursor photoCursor = mContentResolver.query(
//                        photoThumbnailUri, PhotoQuery.PROJECTION, null, null, null);
//                if (photoCursor != null) {
//                    try {
//                        if (photoCursor.moveToFirst()) {
//                            return photoCursor.getBlob(PhotoQuery.PHOTO);
//                        }
//                    } finally {
//                        photoCursor.close();
//                    }
//                } else {
//                    // If the query fails, try streaming the URI directly.
//                    // For remote directory images, this URI resolves to the
//                    // directory provider and the images are loaded by sending
//                    // an openFile call to the provider.
//                    try {
//                        InputStream is = mContentResolver.openInputStream(
//                                photoThumbnailUri);
//                        if (is != null) {
//                            byte[] buffer = new byte[BUFFER_SIZE];
//                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
//                            try {
//                                int size;
//                                while ((size = is.read(buffer)) != -1) {
//                                    baos.write(buffer, 0, size);
//                                }
//                            } finally {
//                                is.close();
//                            }
//                            return baos.toByteArray();
//                        }
//                    } catch (IOException ex) {
//                        // ignore
//                    }
//                }
//                return null;
//            }
//
//            @Override
//            protected void onPostExecute(final byte[] photoBytes) {
//                entry.setPhotoBytes(photoBytes);
//                if (photoBytes != null) {
//                    mPhotoCacheMap.put(photoThumbnailUri, photoBytes);
//                    if (adapter != null)
//                        adapter.notifyDataSetChanged();
//                }
//            }
//        };
//        photoLoadTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
//    }
//
//    protected static void fetchPhoto(final RecipientEntry entry, final Uri photoThumbnailUri, final ContentResolver mContentResolver) {
//        byte[] photoBytes = mPhotoCacheMap.get(photoThumbnailUri);
//        if (photoBytes != null) {
//            entry.setPhotoBytes(photoBytes);
//            return;
//        }
//        final Cursor photoCursor = mContentResolver.query(photoThumbnailUri, PhotoQuery.PROJECTION,
//                null, null, null);
//        if (photoCursor != null) {
//            try {
//                if (photoCursor.moveToFirst()) {
//                    photoBytes = photoCursor.getBlob(PhotoQuery.PHOTO);
//                    entry.setPhotoBytes(photoBytes);
//                    mPhotoCacheMap.put(photoThumbnailUri, photoBytes);
//                }
//            } finally {
//                photoCursor.close();
//            }
//        } else {
//            InputStream inputStream = null;
//            ByteArrayOutputStream outputStream = null;
//            try {
//                inputStream = mContentResolver.openInputStream(photoThumbnailUri);
//                final Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
//
//                if (bitmap != null) {
//                    outputStream = new ByteArrayOutputStream();
//                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
//                    photoBytes = outputStream.toByteArray();
//
//                    entry.setPhotoBytes(photoBytes);
//                    mPhotoCacheMap.put(photoThumbnailUri, photoBytes);
//                }
//            } catch (final FileNotFoundException e) {
//                Log.w(TAG, "Error opening InputStream for photo", e);
//            } finally {
//                try {
//                    if (inputStream != null) {
//                        inputStream.close();
//                    }
//                } catch (IOException e) {
//                    Log.e(TAG, "Error closing photo input stream", e);
//                }
//                try {
//                    if (outputStream != null) {
//                        outputStream.close();
//                    }
//                } catch (IOException e) {
//                    Log.e(TAG, "Error closing photo output stream", e);
//                }
//            }
//        }
//    }
//
//    // TODO: This won't be used at all. We should find better way to quit the thread..
//    /*public void close() {
//        mEntries = null;
//        mPhotoCacheMap.evictAll();
//        if (!sPhotoHandlerThread.quit()) {
//            Log.w(TAG, "Failed to quit photo handler thread, ignoring it.");
//        }
//    }*/
//
//    @Override
//    public int getCount() {
//        final List<RecipientEntry> entries = getEntries();
//        return entries != null ? entries.size() : 0;
//    }
//
//    @Override
//    public RecipientEntry getItem(int position) {
//        return getEntries().get(position);
//    }
//
//    @Override
//    public long getItemId(int position) {
//        return position;
//    }
//
//    @Override
//    public int getViewTypeCount() {
//        return RecipientEntry.ENTRY_TYPE_SIZE;
//    }
//
//    @Override
//    public int getItemViewType(int position) {
//        return getEntries().get(position).getEntryType();
//    }
//
//    @Override
//    public boolean isEnabled(int position) {
//        return getEntries().get(position).isSelectable();
//    }
//
//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//        final RecipientEntry entry = getEntries().get(position);
//
//        final String constraint = mCurrentConstraint == null ? null :
//                mCurrentConstraint.toString();
//
//        return mDropdownChipLayouter.bindView(convertView, parent, entry, position,
//                AdapterType.BASE_RECIPIENT, constraint);
//    }
//
//    public Account getAccount() {
//        return mAccount;
//    }
//
//    public boolean isShowMobileOnly() {
//        return showMobileOnly;
//    }
//
//    public void setShowMobileOnly(boolean showMobileOnly) {
//        this.showMobileOnly = showMobileOnly;
//    }
//
//    public ArrayList<HashTag> autoComplete(String input) {
//        ArrayList<HashTag> resultList = new ArrayList<HashTag>();
//
//        HttpURLConnection conn = null;
//        StringBuilder jsonResults = new StringBuilder();
//        try {
//            StringBuilder sb = new StringBuilder(API_BASE);
//            sb.append("?term=" + URLEncoder.encode(input, "utf8"));
//
//
//            Log.i("AutoComplete", "AutoComplete url check: " + sb.toString());
//            URL url = new URL(sb.toString());
//            conn = (HttpURLConnection) url.openConnection();
//            InputStreamReader in = new InputStreamReader(conn.getInputStream());
//
//            // Load the results into a StringBuilder
//            int read;
//            char[] buff = new char[1024];
//            while ((read = in.read(buff)) != -1) {
//                jsonResults.append(buff, 0, read);
//            }
//        } catch (MalformedURLException e) {
//            Log.e("Auto complete", "Error processing Places API URL", e);
//            return resultList;
//        } catch (IOException e) {
//            Log.e("Auto complete", "Error connecting to Places API", e);
//            return resultList;
//        } finally {
//            if (conn != null) {
//                conn.disconnect();
//            }
//        }
//
//        try {
//            // Create a JSON object hierarchy from the results
//            Log.i("AutoComplete", "AutoComplete"+jsonResults.toString());
//            JSONArray predsJsonArray = null;
//            if(jsonResults.toString().contains("data")){
//                predsJsonArray = new JSONObject(jsonResults.toString()).getJSONArray("data");
//            }
//            if (predsJsonArray!=null&&predsJsonArray.length()>0){
//                hashTagJsonArray = predsJsonArray;
//                // Extract the Place descriptions from the results
//                resultList = new ArrayList<String>(predsJsonArray.length());
//                for (int i = 0; i < predsJsonArray.length(); i++){
//                    if(predsJsonArray.getJSONObject(i).has("name")){
//                        resultList.add(predsJsonArray.getJSONObject(i).getString("name"));
//                    }
//                    if(predsJsonArray.getJSONObject(i).has("ENGLISH_NAME_EN")){
//                        resultList.add(predsJsonArray.getJSONObject(i).getString("ENGLISH_NAME_EN"));
//                    }
//                }
//            }
//        } catch (JSONException e) {
//            Log.e("Auto complete", "Cannot process JSON results", e);
//        }
//        return resultList;
//    }
//}
